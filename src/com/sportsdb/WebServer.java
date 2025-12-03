package com.sportsdb;

import com.sportsdb.dao.PlayerDAOImpl;
import com.sportsdb.dao.StatDAOImpl;
import com.sportsdb.model.Player;
import com.sportsdb.model.Stat;
import com.sportsdb.service.PlayerService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class WebServer {

    public static void main(String[] args) throws Exception {
        // Service layer (backend)
        PlayerService playerService =
                new PlayerService(new PlayerDAOImpl(), new StatDAOImpl());

        // Start HTTP server on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Routes
        server.createContext("/", new HomeHandler());
        server.createContext("/players", new PlayersHandler(playerService));

        // Admin routes (CRUD)
        server.createContext("/admin", new AdminPageHandler(playerService));
        server.createContext("/admin/add-player", new AddPlayerHandler(playerService));
        server.createContext("/admin/update-player", new UpdatePlayerHandler(playerService));
        server.createContext("/admin/delete-player", new DeletePlayerHandler());

        server.setExecutor(null); // default
        server.start();
        System.out.println("Server running at http://localhost:8080");
    }

    // ---------------------------------------------------------------------
    // Home (dashboard)
    // ---------------------------------------------------------------------
    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>")
              .append("<html lang='en'>")
              .append("<head>")
              .append("<meta charset='UTF-8'/>")
              .append("<title>NBA Player Stats</title>")
              .append("<style>")
              .append("*{box-sizing:border-box;margin:0;padding:0;}")
              .append("body{font-family:system-ui,-apple-system,BlinkMacSystemFont,'Segoe UI',sans-serif;")
              .append("background:linear-gradient(135deg,#0f172a,#1e3a8a);color:#e5e7eb;")
              .append("min-height:100vh;display:flex;align-items:center;justify-content:center;padding:24px;}")
              .append(".card{background:rgba(15,23,42,0.9);border-radius:18px;")
              .append("box-shadow:0 20px 40px rgba(0,0,0,0.4);padding:32px 40px;max-width:640px;width:100%;}")
              .append("h1{font-size:2rem;margin-bottom:12px;}")
              .append("p{color:#9ca3af;margin-bottom:20px;}")
              .append(".buttons{display:flex;gap:12px;flex-wrap:wrap;}")
              .append("a.button{display:inline-block;padding:10px 18px;border-radius:999px;")
              .append("background:#22c55e;color:#0f172a;text-decoration:none;font-weight:600;")
              .append("transition:transform 0.1s ease,box-shadow 0.1s ease,background 0.2s;")
              .append("box-shadow:0 10px 25px rgba(34,197,94,0.4);}")
              .append("a.button.secondary{background:#38bdf8;box-shadow:0 10px 25px rgba(56,189,248,0.4);}")
              .append("a.button:hover{transform:translateY(-1px);}")
              .append(".badge{display:inline-flex;align-items:center;gap:6px;padding:4px 10px;")
              .append("border-radius:999px;background:rgba(55,65,81,0.7);color:#e5e7eb;font-size:0.8rem;margin-bottom:16px;}")
              .append(".badge span{width:6px;height:6px;border-radius:999px;background:#22c55e;}")
              .append("</style>")
              .append("</head>")
              .append("<body>")
              .append("<div class='card'>")
              .append("<div class='badge'><span></span>Live MySQL-backed app</div>")
              .append("<h1>NBA Player Stats Dashboard</h1>")
              .append("<p>Browse NBA players from a MySQL database and search by player name. ")
              .append("Use the admin panel to add, edit, or delete players and their stats.</p>")
              .append("<div class='buttons'>")
              .append("<a class='button' href='/players'>Open Players Explorer →</a>")
              .append("<a class='button secondary' href='/admin'>Open Admin Panel</a>")
              .append("</div>")
              .append("</div>")
              .append("</body>")
              .append("</html>");

            sendHtml(exchange, sb.toString());
        }
    }

    // ---------------------------------------------------------------------
    // Players list + search (by player name only)
    // ---------------------------------------------------------------------
    static class PlayersHandler implements HttpHandler {
        private final PlayerService playerService;

        public PlayersHandler(PlayerService playerService) {
            this.playerService = playerService;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                URI uri = exchange.getRequestURI();
                String qString = uri.getQuery();
                String search = getQueryParam(qString, "q"); // player name

                List<Player> players = playerService.searchByNameOrTeam(search);
                String safeSearch = (search == null) ? "" : escapeHtml(search);

                StringBuilder sb = new StringBuilder();
                sb.append("<!DOCTYPE html>")
                  .append("<html lang='en'>")
                  .append("<head>")
                  .append("<meta charset='UTF-8'/>")
                  .append("<title>Players</title>")
                  .append("<style>")
                  .append("*{box-sizing:border-box;margin:0;padding:0;}")
                  .append("body{font-family:system-ui,-apple-system,BlinkMacSystemFont,'Segoe UI',sans-serif;")
                  .append("background:radial-gradient(circle at top,#1d4ed8,#020617);color:#e5e7eb;")
                  .append("min-height:100vh;padding:24px;}")
                  .append(".layout{max-width:1100px;margin:0 auto;}")
                  .append("header{display:flex;justify-content:space-between;align-items:center;margin-bottom:18px;}")
                  .append("header h1{font-size:1.6rem;}")
                  .append("header a{color:#9ca3af;text-decoration:none;font-size:0.9rem;}")
                  .append("header a:hover{color:#e5e7eb;}")
                  .append(".search-card{background:rgba(15,23,42,0.95);border-radius:18px;padding:16px 20px;")
                  .append("margin-bottom:16px;box-shadow:0 16px 40px rgba(0,0,0,0.5);border:1px solid rgba(148,163,184,0.3);}")
                  .append(".search-label{font-size:0.85rem;color:#9ca3af;margin-bottom:6px;}")
                  .append(".search-row{display:flex;gap:10px;}")
                  .append(".search-input{flex:1;padding:10px 12px;border-radius:999px;")
                  .append("border:1px solid rgba(148,163,184,0.8);background:rgba(15,23,42,0.9);")
                  .append("color:#e5e7eb;outline:none;font-size:0.95rem;}")
                  .append(".search-input::placeholder{color:#6b7280;}")
                  .append(".search-button{padding:10px 16px;border-radius:999px;border:none;")
                  .append("background:#22c55e;color:#0f172a;font-weight:600;cursor:pointer;font-size:0.95rem;}")
                  .append(".search-button:hover{background:#16a34a;}")
                  .append(".results-card{margin-top:14px;background:rgba(15,23,42,0.96);border-radius:18px;overflow:hidden;")
                  .append("box-shadow:0 20px 45px rgba(0,0,0,0.6);border:1px solid rgba(148,163,184,0.3);}")
                  .append("table{width:100%;border-collapse:collapse;}")
                  .append("thead{background:rgba(15,23,42,0.98);}")
                  .append("th,td{padding:10px 12px;text-align:left;font-size:0.9rem;}")
                  .append("th{color:#9ca3af;font-weight:500;border-bottom:1px solid rgba(55,65,81,0.9);}")
                  .append("tbody tr:nth-child(even){background:rgba(30,41,59,0.7);}")
                  .append("tbody tr:nth-child(odd){background:rgba(15,23,42,0.9);}")
                  .append("tbody tr:hover{background:rgba(59,130,246,0.35);}")
                  .append(".pill{display:inline-block;padding:3px 8px;border-radius:999px;")
                  .append("background:rgba(37,99,235,0.25);color:#bfdbfe;font-size:0.8rem;}")
                  .append(".sub{color:#9ca3af;font-size:0.8rem;margin-top:6px;}")
                  .append(".empty{padding:16px;text-align:center;color:#9ca3af;}")
                  .append("</style>")
                  .append("</head>")
                  .append("<body>")
                  .append("<div class='layout'>")
                  .append("<header>")
                  .append("<h1>NBA Players</h1>")
                  .append("<a href='/'>← Back to dashboard</a>")
                  .append("</header>")
                  .append("<div class='search-card'>")
                  .append("<div class='search-label'>Search by player name:</div>")
                  .append("<form class='search-row' method='get' action='/players'>")
                  .append("<input class='search-input' type='text' name='q' ")
                  .append("placeholder='Type a player name...' value='")
                  .append(safeSearch)
                  .append("'/>")
                  .append("<button class='search-button' type='submit'>Search</button>")
                  .append("</form>")
                  .append("<div class='sub'>");

                if (search == null || search.trim().isEmpty()) {
                    sb.append("Showing all players.");
                } else {
                    sb.append("Showing results for &quot;").append(safeSearch).append("&quot;.");
                }

                sb.append("</div></div>") // close sub and search-card
                  .append("<div class='results-card'>");

                if (players.isEmpty()) {
                    sb.append("<div class='empty'>No players matched your search.</div>");
                } else {
                    sb.append("<table>")
                      .append("<thead><tr>")
                      .append("<th>ID</th><th>Player</th><th>Team</th>")
                      .append("<th>Pos</th><th>Age</th><th>PTS</th><th>AST</th><th>GP</th>")
                      .append("</tr></thead><tbody>");

                    for (Player p : players) {
                        String teamName = getTeamNameForId(p.getTeamId());
                        Stat s = null;
                        try {
                            s = playerService.getStatsForPlayer(p.getPlayerId());
                        } catch (Exception ignored) { }

                        String pts = (s == null) ? "–" : String.format("%.1f", s.getGoals());
                        String ast = (s == null) ? "–" : String.format("%.1f", s.getAssists());
                        String gp  = (s == null) ? "–" : String.valueOf(s.getMatches());

                        sb.append("<tr>")
                          .append("<td>").append(p.getPlayerId()).append("</td>")
                          .append("<td>").append(escapeHtml(p.getName())).append("</td>")
                          .append("<td>").append(escapeHtml(teamName)).append("</td>")
                          .append("<td><span class='pill'>")
                          .append(escapeHtml(p.getPosition()))
                          .append("</span></td>")
                          .append("<td>").append(p.getAge()).append("</td>")
                          .append("<td>").append(pts).append("</td>")
                          .append("<td>").append(ast).append("</td>")
                          .append("<td>").append(gp).append("</td>")
                          .append("</tr>");
                    }

                    sb.append("</tbody></table>");
                }

                sb.append("</div>") // results-card
                  .append("</div>") // layout
                  .append("</body>")
                  .append("</html>");

                sendHtml(exchange, sb.toString());

            } catch (Exception e) {
                e.printStackTrace();
                sendSimpleHtml(exchange, "Server error: " + escapeHtml(e.getMessage()));
            }
        }
    }

    // ---------------------------------------------------------------------
    // Admin page (forms for Add / Update / Delete)
    // ---------------------------------------------------------------------
    static class AdminPageHandler implements HttpHandler {
        private final PlayerService playerService;

        public AdminPageHandler(PlayerService playerService) {
            this.playerService = playerService;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>")
              .append("<html lang='en'>")
              .append("<head>")
              .append("<meta charset='UTF-8'/>")
              .append("<title>Admin - Manage Players</title>")
              .append("<style>")
              .append("body{font-family:system-ui,-apple-system,BlinkMacSystemFont,'Segoe UI',sans-serif;")
              .append("background:#020617;color:#e5e7eb;padding:24px;}")
              .append("h1{margin-bottom:16px;}")
              .append("a{color:#60a5fa;text-decoration:none;}")
              .append("a:hover{color:#bfdbfe;}")
              .append(".grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(280px,1fr));")
              .append("gap:16px;margin-top:16px;}")
              .append(".card{background:#0f172a;border-radius:14px;padding:16px 18px;border:1px solid #1f2937;}")
              .append("label{display:block;font-size:0.85rem;margin-bottom:4px;}")
              .append("input{width:100%;padding:6px 8px;border-radius:8px;border:1px solid #4b5563;")
              .append("background:#020617;color:#e5e7eb;margin-bottom:8px;}")
              .append("button{padding:6px 10px;border-radius:999px;border:none;background:#22c55e;")
              .append("color:#020617;font-weight:600;cursor:pointer;}")
              .append("button:hover{background:#16a34a;}")
              .append(".hint{font-size:0.8rem;color:#9ca3af;margin-top:6px;}")
              .append("</style>")
              .append("</head>")
              .append("<body>")
              .append("<a href='/'>← Back to Dashboard</a>")
              .append("<h1>Admin: Manage Players</h1>")
              .append("<div class='hint'>Team names come from the Teams table (e.g. LAL, BOS).</div>")
              .append("<div class='grid'>")

              // Add
              .append("<div class='card'>")
              .append("<h3>Add Player</h3>")
              .append("<form method='get' action='/admin/add-player'>")
              .append("<label>Name</label><input name='name' required />")
              .append("<label>Age</label><input name='age' type='number' required />")
              .append("<label>Position (e.g. PG, SG)</label><input name='position' required />")
              .append("<label>Team Name</label><input name='teamName' required />")
              .append("<label>Points (PTS)</label><input name='pts' type='number' step='0.1' required />")
              .append("<label>Assists (AST)</label><input name='ast' type='number' step='0.1' required />")
              .append("<label>Games Played (GP)</label><input name='gp' type='number' required />")
              .append("<button type='submit'>Add</button>")
              .append("</form>")
              .append("</div>")

              // Update
              .append("<div class='card'>")
              .append("<h3>Update Player</h3>")
              .append("<form method='get' action='/admin/update-player'>")
              .append("<label>Player ID</label><input name='id' type='number' required />")
              .append("<label>New Name</label><input name='name' required />")
              .append("<label>New Age</label><input name='age' type='number' required />")
              .append("<label>New Position</label><input name='position' required />")
              .append("<label>New Team Name</label><input name='teamName' required />")
              .append("<label>New Points (PTS)</label><input name='pts' type='number' step='0.1' required />")
              .append("<label>New Assists (AST)</label><input name='ast' type='number' step='0.1' required />")
              .append("<label>New Games Played (GP)</label><input name='gp' type='number' required />")
              .append("<button type='submit'>Update</button>")
              .append("</form>")
              .append("</div>")

              // Delete
              .append("<div class='card'>")
              .append("<h3>Delete Player</h3>")
              .append("<form method='get' action='/admin/delete-player'>")
              .append("<label>Player Name</label><input name='name' required />")
              .append("<button type='submit'>Delete</button>")
              .append("</form>")
              .append("</div>")

              .append("</div>") // grid
              .append("</body>")
              .append("</html>");

            sendHtml(exchange, sb.toString());
        }
    }

    // ---------------------------------------------------------------------
    // Add / Update / Delete handlers
    // ---------------------------------------------------------------------
    static class AddPlayerHandler implements HttpHandler {
        private final PlayerService playerService;

        public AddPlayerHandler(PlayerService playerService) {
            this.playerService = playerService;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String qs = exchange.getRequestURI().getQuery();
                String name = getQueryParam(qs, "name");
                String ageStr = getQueryParam(qs, "age");
                String position = getQueryParam(qs, "position");
                String teamName = getQueryParam(qs, "teamName");
                String ptsStr = getQueryParam(qs, "pts");
                String astStr = getQueryParam(qs, "ast");
                String gpStr  = getQueryParam(qs, "gp");

                int age = Integer.parseInt(ageStr);
                double pts = Double.parseDouble(ptsStr);
                double ast = Double.parseDouble(astStr);
                int gp = Integer.parseInt(gpStr);

                int teamId = getOrCreateTeamIdByName(teamName);

                Player p = new Player();
                p.setName(name);
                p.setAge(age);
                p.setPosition(position);
                p.setTeamId(teamId);
                p.setFaceCode("N/A"); // not used in UI

                // create player via service
                playerService.addPlayer(p);

                // find the latest player with that name & team and upsert stats
                int playerId = getLatestPlayerIdByNameAndTeam(name, teamId);
                upsertStatsForPlayer(playerId, pts, ast, gp);

                sendSimpleHtml(exchange,
                        "Player and stats added successfully. <a href='/admin'>Back</a>");
            } catch (Exception e) {
                e.printStackTrace();
                sendSimpleHtml(exchange,
                        "Error adding player: " + escapeHtml(e.getMessage()) +
                                " <a href='/admin'>Back</a>");
            }
        }
    }

    static class UpdatePlayerHandler implements HttpHandler {
        private final PlayerService playerService;

        public UpdatePlayerHandler(PlayerService playerService) {
            this.playerService = playerService;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String qs = exchange.getRequestURI().getQuery();
                String idStr = getQueryParam(qs, "id");
                String name = getQueryParam(qs, "name");
                String ageStr = getQueryParam(qs, "age");
                String position = getQueryParam(qs, "position");
                String teamName = getQueryParam(qs, "teamName");
                String ptsStr = getQueryParam(qs, "pts");
                String astStr = getQueryParam(qs, "ast");
                String gpStr  = getQueryParam(qs, "gp");

                int id = Integer.parseInt(idStr);
                int age = Integer.parseInt(ageStr);
                double pts = Double.parseDouble(ptsStr);
                double ast = Double.parseDouble(astStr);
                int gp = Integer.parseInt(gpStr);

                int teamId = getOrCreateTeamIdByName(teamName);

                Player p = new Player();
                p.setPlayerId(id);
                p.setName(name);
                p.setAge(age);
                p.setPosition(position);
                p.setTeamId(teamId);
                p.setFaceCode("N/A");

                // update player via service
                playerService.updatePlayer(p);

                // update or insert stats for this player
                upsertStatsForPlayer(id, pts, ast, gp);

                sendSimpleHtml(exchange,
                        "Player and stats updated successfully. <a href='/admin'>Back</a>");
            } catch (Exception e) {
                e.printStackTrace();
                sendSimpleHtml(exchange,
                        "Error updating player: " + escapeHtml(e.getMessage()) +
                                " <a href='/admin'>Back</a>");
            }
        }
    }

    // delete by player name (not ID)
    static class DeletePlayerHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String qs = exchange.getRequestURI().getQuery();
                String name = getQueryParam(qs, "name");
                deletePlayerByName(name);

                sendSimpleHtml(exchange,
                        "Player(s) named \"" + escapeHtml(name) +
                                "\" deleted successfully. <a href='/admin'>Back</a>");
            } catch (Exception e) {
                e.printStackTrace();
                sendSimpleHtml(exchange,
                        "Error deleting player: " + escapeHtml(e.getMessage()) +
                                " <a href='/admin'>Back</a>");
            }
        }
    }

    // ---------------------------------------------------------------------
    // Helpers: query param, HTML escaping, send HTML
    // ---------------------------------------------------------------------
    private static String getQueryParam(String queryString, String key) {
        if (queryString == null || queryString.isEmpty()) return null;
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx > 0) {
                String k = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                if (k.equals(key)) {
                    String value = pair.substring(idx + 1);
                    return URLDecoder.decode(value, StandardCharsets.UTF_8);
                }
            }
        }
        return null;
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private static void sendSimpleHtml(HttpExchange exchange, String body) throws IOException {
        String html = "<!DOCTYPE html><html><body style='font-family:system-ui;"
                + "background:#020617;color:#e5e7eb;padding:24px;'>" + body
                + "</body></html>";
        sendHtml(exchange, html);
    }

    private static void sendHtml(HttpExchange exchange, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    // ---------------------------------------------------------------------
    // Helper: get team name by TeamID
    // ---------------------------------------------------------------------
    private static String getTeamNameForId(int teamId) {
        String sql = "SELECT Name FROM Teams WHERE TeamID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Team " + teamId;
    }

    // ---------------------------------------------------------------------
    // Helper: resolve or create TeamID from a team name
    // ---------------------------------------------------------------------
    private static int getOrCreateTeamIdByName(String teamName) throws SQLException {
        if (teamName == null || teamName.trim().isEmpty()) {
            throw new SQLException("Team name is required");
        }

        // try to find existing
        String selectSql = "SELECT TeamID FROM Teams WHERE Name = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setString(1, teamName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TeamID");
                }
            }
        }

        // insert new team if not found
        String insertSql = "INSERT INTO Teams (Name) VALUES (?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, teamName);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Failed to create/find team for name: " + teamName);
    }

    // ---------------------------------------------------------------------
    // Helper: get newest PlayerID for name + team (for add)
    // ---------------------------------------------------------------------
    private static int getLatestPlayerIdByNameAndTeam(String name, int teamId) throws SQLException {
        String sql = "SELECT PlayerID FROM Players WHERE Name = ? AND TeamID = ? ORDER BY PlayerID DESC LIMIT 1";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("PlayerID");
                }
            }
        }
        throw new SQLException("Player not found after insert (name=" + name + ", teamId=" + teamId + ")");
    }

    // ---------------------------------------------------------------------
    // Helper: upsert stats for a player (update if exists, else insert)
    // ---------------------------------------------------------------------
    private static void upsertStatsForPlayer(int playerId, double pts, double ast, int gp) throws SQLException {
        try (Connection conn = DBConnectionUtil.getConnection()) {
            // first try update
            String updateSql = "UPDATE Stats SET Goals = ?, Assists = ?, Matches = ? WHERE PlayerID = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setDouble(1, pts);
                ps.setDouble(2, ast);
                ps.setInt(3, gp);
                ps.setInt(4, playerId);
                int updated = ps.executeUpdate();
                if (updated > 0) {
                    return; // updated existing row
                }
            }

            // if nothing updated, insert new stats
            String insertSql = "INSERT INTO Stats (PlayerID, Goals, Assists, Matches) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, playerId);
                ps.setDouble(2, pts);
                ps.setDouble(3, ast);
                ps.setInt(4, gp);
                ps.executeUpdate();
            }
        }
    }

    // ---------------------------------------------------------------------
    // Helper: delete player(s) by name and their stats
    // ---------------------------------------------------------------------
    private static void deletePlayerByName(String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new SQLException("Player name is required");
        }

        try (Connection conn = DBConnectionUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // find all player IDs with that name
                String selectSql = "SELECT PlayerID FROM Players WHERE Name = ?";
                try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                    ps.setString(1, name);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int playerId = rs.getInt("PlayerID");
                            // delete stats for this player
                            try (PreparedStatement psStats =
                                         conn.prepareStatement("DELETE FROM Stats WHERE PlayerID = ?")) {
                                psStats.setInt(1, playerId);
                                psStats.executeUpdate();
                            }
                        }
                    }
                }

                // delete players with this name
                try (PreparedStatement psDel =
                             conn.prepareStatement("DELETE FROM Players WHERE Name = ?")) {
                    psDel.setString(1, name);
                    psDel.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
