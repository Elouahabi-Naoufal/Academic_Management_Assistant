# Academic Management Assistant

A comprehensive Android app for managing academic schedules, grades, assignments, and events.

## Features

### 📅 Timeline Management
- Real-time countdown to next event
- Unified timeline showing classes, exams, assignments, and makeup sessions
- Live updates every second
- Smart event prioritization

### 📊 Grade Tracking
- Record and track grades across subjects
- Automatic percentage calculation
- Overall GPA tracking
- Performance analytics

### 🗓️ Schedule View
- Weekly schedule overview
- Class timetable management
- Room and teacher information

### 📝 Assignment Management
- Task creation and tracking
- Due date reminders
- Priority levels
- Subject categorization

### 🔔 Smart Notifications
- Upcoming event alerts
- Assignment due date reminders
- Customizable notification preferences

## Technical Architecture

### Models
- `ClassSessionModel` - Class schedule data
- `Assignment` - Homework and project tracking
- `Exam` - Test and examination management
- `Rattrapage` - Makeup session handling
- `Grade` - Academic performance tracking
- `TimelineItem` - Unified event representation

### Fragments
- `TimelineFragment` - Main dashboard with upcoming events
- `GradesFragment` - Grade tracking and analytics
- `ScheduleFragment` - Weekly schedule view
- `AssignmentsFragment` - Task management

### Key Features
- **Data Persistence**: JSON-based local storage
- **Material Design**: Modern UI with dark/light theme support
- **Navigation**: Bottom navigation for easy access
- **Real-time Updates**: Live countdown and event tracking
- **Notifications**: System notifications for reminders

## Installation

1. Clone the repository
2. Open in Android Studio
3. Build and run on Android device (API 30+)

## Usage

1. **Timeline**: View upcoming events and live countdown
2. **Grades**: Add and track academic performance
3. **Schedule**: Manage weekly class timetable
4. **Tasks**: Create and manage assignments

## Future Enhancements

- Cloud synchronization
- Calendar integration
- Study time tracking
- Grade prediction algorithms
- Social features for study groups