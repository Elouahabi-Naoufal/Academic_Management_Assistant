#!/usr/bin/env python3

import sqlite3
import os

# Create local database for testing
db_path = "academic_management.db"

# Connect to database
conn = sqlite3.connect(db_path)
cursor = conn.cursor()

# Create tables if they don't exist
cursor.execute("""CREATE TABLE IF NOT EXISTS module (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(255) NOT NULL)""")

cursor.execute("""CREATE TABLE IF NOT EXISTS teacher (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name VARCHAR(255) NOT NULL,
    image_path VARCHAR(255))""")

cursor.execute("""CREATE TABLE IF NOT EXISTS class (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    module_id INTEGER NOT NULL,
    teacher_id INTEGER NOT NULL,
    location VARCHAR(255),
    weekday INTEGER NOT NULL,
    start_time TEXT NOT NULL,
    end_time TEXT NOT NULL,
    archive_session_id INTEGER,
    FOREIGN KEY (module_id) REFERENCES module(id),
    FOREIGN KEY (teacher_id) REFERENCES teacher(id))""")

# Clear existing data
cursor.execute("DELETE FROM class")
cursor.execute("DELETE FROM module")
cursor.execute("DELETE FROM teacher")

# Insert modules
modules = [
    "Advanced English 1",
    "Machine Learning", 
    "Administration UNIX",
    "Analyse des données",
    "Recherche opérationnelle",
    "NoSQL",
    "Introduction et éthique de l'IA",
    "Dev mobile",
    "Virtualisation",
    "DOTNET",
    "Communication professionnelle 3",
    "Programmation Avancée",
    "Administration oracle 1"
]

for module in modules:
    cursor.execute("INSERT INTO module (name) VALUES (?)", (module,))

# Insert teachers
teachers = [
    "Bilal ED-DERAOUAY",
    "Redouane EDDAHBI",
    "CHAHBOUN Asaad", 
    "Abdelhak TALI",
    "Ilias AARAB",
    "Houda ZAIM",
    "Sara IBN EL AHRACHE",
    "Zakia EL UAHHABI",
    "Zakariae TBATOU",
    "Abderrahman EZZAMRI",
    "Chaimae KHALLOUQ",
    "Houssam BAZZA",
    "Hassan BADIR"
]

for teacher in teachers:
    cursor.execute("INSERT INTO teacher (full_name) VALUES (?)", (teacher,))

# Get module and teacher IDs
def get_module_id(name):
    cursor.execute("SELECT id FROM module WHERE name = ?", (name,))
    return cursor.fetchone()[0]

def get_teacher_id(name):
    cursor.execute("SELECT id FROM teacher WHERE full_name = ?", (name,))
    return cursor.fetchone()[0]

# Insert classes (weekday: Monday=2, Tuesday=3, Wednesday=4, Thursday=5, Friday=6, Saturday=7)
classes = [
    ("Advanced English 1", "Bilal ED-DERAOUAY", "SC-74", 2, "08:30", "10:00"),
    ("Machine Learning", "Redouane EDDAHBI", "SC-74", 2, "10:15", "11:45"),
    ("Administration UNIX", "CHAHBOUN Asaad", "SC-34", 3, "08:30", "10:00"),
    ("Analyse des données", "Abdelhak TALI", "SC-74", 3, "10:45", "12:15"),
    ("Recherche opérationnelle", "Ilias AARAB", "SC-31", 3, "14:30", "16:00"),
    ("NoSQL", "Houda ZAIM", "SC-31", 3, "16:15", "17:45"),
    ("Introduction et éthique de l'IA", "Sara IBN EL AHRACHE", "SC-64", 4, "09:00", "10:30"),
    ("Dev mobile", "Zakia EL UAHHABI", "SC-52", 4, "10:45", "12:15"),
    ("Virtualisation", "Zakariae TBATOU", "SC-53", 4, "12:30", "14:00"),
    ("DOTNET", "Abderrahman EZZAMRI", "SC-22", 5, "12:00", "14:00"),
    ("Communication professionnelle 3", "Chaimae KHALLOUQ", "SC-62", 5, "14:30", "16:00"),
    ("Programmation Avancée", "Houssam BAZZA", "SC-34", 6, "14:30", "16:00"),
    ("Programmation Avancée", "Houssam BAZZA", "SC-34", 6, "16:15", "18:15"),
    ("Administration oracle 1", "Hassan BADIR", "SC-71", 7, "08:30", "10:30"),
]

for class_data in classes:
    module_name, teacher_name, location, weekday, start_time, end_time = class_data
    module_id = get_module_id(module_name)
    teacher_id = get_teacher_id(teacher_name)
    
    cursor.execute("""
        INSERT INTO class (title, module_id, teacher_id, location, weekday, start_time, end_time)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """, (module_name, module_id, teacher_id, location, weekday, start_time, end_time))

# Commit changes
conn.commit()
conn.close()

print("Database populated successfully!")
print(f"Database created at: {os.path.abspath(db_path)}")