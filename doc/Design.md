# Spaced Repetition Design

## Team Contact Info

Sarah Reed: ree16014@byui.edu
Adrian Ruiz: adrianruizb18@gmail.com
Tom Metge: tom@metge.us

## Problem Statement

We would like to work on an application that sends time-based reminders to help promote recall of information. The timing of these reminders is derived from research into spaced repetition. Information to recall is provided by the user and can be anything - text, image, link, audio, etc.

Our project sponsor is Virginia Metge, the 17-year-old daughter of Tom Metge. She would like to use the project to improve her memory as she studies her school work.

## Required Features

The minimum viable product will:

1. Collect information from the user:
  - Collect text-based information
  - Collect a text-based summary of the information (for use in the reminder)
2. Render reminders on a algorithmically-generated schedule:
  - Algorithm for time-spacing reminders
  - Display the user-provided summary for each item
  - Provide an interactive way for the user to view the full text or original information

## Stretch Goals

In addition to the MVP described above, we would like to:

1. Collection information from the user (in additional formats):
  - Collect scheduling or calendaring information
  - Collect photos
  - Collect PDFs
2. Render reminders:
  - Provide an interactive way for the user to view the additional formats via the reminder
  - Set a configurable timer during which the reminder remains on screen (so the user can't dismiss and ignore it)
3. Syncing data across devices:
  - Use a data API to store and sync collected information and reminder schedules across multiple devices.

## Overall Approach

### Persistence

For local and cloud storage, we will use Firebase.

### Authentication

To identify and authenticate the user, we will integrate the stock Android login flow (via Android Studio) with Firebase.

### Reminders

Firebase provides support for local and remote notifications. We will integrate both to provide a seamless user experience (see user cases).

## User Stories

### Primary Use Case (adding and consuming a text-based reminder)

Virginia (our sponsor) is struggling to recall information necessary to succeed in several classes in school. She is specifically concerned with Math. She opens the Spaced Repetition app and:

1. Presses the "+" button to add a new tidbit for recall
2. Fills in the text summary with: "Area of a circle"
3. Fills in the text to recall with: "Area = Pi * (Radius * Radius)"
4. She taps save and the the new tidbit is added to her list
5. She closes the app and continues with her day
6. In the evening, the app presents a reminder with "Area of a circle" included
7. Virginia attempts to recall the equation and taps the button in the reminder to check
8. The app presents the text to recall ("Area = Pi * (Radius * Radius)").
9. Virginia again closes the app
10. The following morning, the app presents a reminder with "Area of a circle" included
11. This sequence is repeated two days later, then 4 days, and again one week later

### Primary Use Case (specifying a termination date for a reminder)

1. Steps 1-3 of the above case
2. She taps on the option to end the reminder on a fixed or relative date.
3. Steps 4-11
4. On the date of the reminder's termination, the app stops presenting the reminder.

### Primary Use Case (removing an existing reminder)

1. She selects a reminder from the list of existing reminders
2. The app shows a view asking her to edit or remove the selected reminder.
3. She select "Remove"
4. The reminder is removed.

### Primay Use Case (editing an existing reminder)

1. She selects a reminder from the list of existing reminders
2. The app shows a view asking her to edit or remove the selected reminder.
3. She selects "Edit"
4. The app presents the same interface as for adding a reminder, pre-populated with the data from the existing reminder.
5. She taps "Save"

### Stretch Goal Case (adding and consuming non-text reminders)

As a stretch goal, we would like the app to accept additional formats of information. The user story for this case is as follows:

1. Virginia presses the "+" button to add a new tidbit for recall
2. She fills in the text summary with: "U.S. population in the 20th century"
3. She taps "Add photo" and adds a JPG with a chart showing the population of the U.S. throughout the 20th century
4. She taps save and the new tidbit is added to her list
5. The app offers reminders as described in the first story (the base case)

## Interface Elements

### Views

Mock-ups for each view is attached under the title in parentheses.

1. (Screen 1) List of reminders with a plus button to add a new reminder
2. (Screen 2) Add/edit form with save button
3. (Screen 3) Edit/remove pop-up (cancel by tapping outside the view)
4. (Screen 4) Reminder notification
5. (Screen 5) Login
6. (Screen 6) Create account

## Data Model

- Reminder
  - User
  - Summary
  - Content
    - Text
    - Image
    - Document
  - Creation time
  - Time to live (TTL)
  - Last notification date
  - Current period

- User
  - Username
  - First name
  - Last name
  - Auth token

- Repetition Schedule
  - Every day for 7 days
  - Every week for 4 weeks
  - Every month perpetually (until TTL)

## Classes
 
### Models

- Reminder (outline in data model)
- User (outline in data model)
- Repetition Schedule

### Views

- Login <Activity>
  - Username
  - Password
  - (Button) 

- Create Account <Activity>
  - Username
  - First name
  - Last name
  - Password
  - (Button) Save
  - (Button) Cancel

- Main <Activity>
  - (Button) Sign out
  - List of reminders
  - (Button) Add

- Reminder <Activity>
  - Summary
  - Content
  - Time to live
  - (Button) Save

- Pop-up <Fragment>
  - Summary
  - (Button) Edit
  - (Button) Remove

- Notification <Fragment>
  - Summary
  - (Button) Dismiss
  - (Button) Check

### Controllers

- User
  - Sign in
  - Sign out
  - Create account

- Reminder
  - List of reminders
  - Pushing notifications
  - Add / edit reminders

## Milestones

1. (Lesson 09)
  - Create all views with all visual elements present
  - Use mock data (not live data)
  - Main view and associated controls are fully functional (with mock data)
  - Initial feedback from stakeholder
2. (Lesson 11)
  - Integrate live data
  - Add unit test coverage for data model
  - All views and controls are fully functional
  - Follow-up feedback from stakeholder
3. (Lesson 13)
  - Test on different devices and device classes
  - Final testing and feedback from stakeholder
