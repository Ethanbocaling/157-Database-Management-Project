# 157-Database-Management-Project

# Project Title and Team Members

## Project Title  
Sports Player Management System  

## Team Members  
Ethan Bocaling, Justin Groves, Dylan Weitz  

---

# Introduction

## Overview  
We aim to implement a database system that enables the management of players and teams and provides an easy way to search for their stats.  

## Purpose  
Make the tracking of teams, players, and their corresponding stats easier.  

---

# Objectives

## Primary Goals  
Store player info, team info, and performance statistics.  

## Functionality  
Search for players using their image, add/edit players, and record their stats.  

---

# Proposed Database Schema

## Entities  
Players, Teams, Stats, Contracts  

## Attributes  
- **Players:** PlayerID, Name, Age, Position, Alma Mater, Player Facial Embedding  
- **Teams:** TeamID, Name, City, Coach, Number of Tournaments Won, Relative Ranking  
- **Stats:** StatID, PlayerID, Goals, Assists, Matches Played, Relative Player Ranking, Win Percentage  
- **Contracts:** ContractID, PlayerID, Salary, StartDate, EndDate  

## Relationships  
- Players — one to many → Contracts  
- Players — one to one → Stats  
- Teams — one to many → Players  

---

# Functional Requirements

## Users  
Coaches / Administrators / Everyday People  

## Key Functionalities  
- Alter player, team, and stat data  
- Search by position or face  
- Output search results  

---

# Non-Functional Requirements

## Security  
Role-based access to the database.  

## Scalability  
Provide a method to give entities new attributes.  

## Performance  
Enable concurrent access to the database without delay.  

---

# Tools and Technologies

## Stack  
MySQL, Java JDBC  

## Environment  
Visual Studio Code (VSC)  

---

# Conclusion

## Outcomes  
A functional player management system.  

## Next Steps  
- Further design the database schema  
- Collect data  
- Design web application  
