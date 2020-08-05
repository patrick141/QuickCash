# QuickCash

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Need to make money fast? QuickCash is an app that allows people to find easy-quick money jobs. Users can request specific people to help complete tasks and pay them afterwards. Examples include but not limited to (Car-wash, dog-walking, move heavy items, etc). Users can also ask for reccuring jobs such as babysitter, translator, etc.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Matching. This app is for finding easy and quick jobs.
- **Mobile:** This app is in mobile so users can quickly find job posts and create job requests. Camera is used.
- **Story:** Allows users to find ways to make quick cash by searching for small tasks that other users post.
- **Market:** This app allows people to market their small tasks and post to audience who are willing to do small tasks for money. This app can be useful for all ages.
- **Habit:** Users can post throughout the day and complete up to 5 tasks per day.
- **Scope:** This app starts with searching for job tasks but helps people connect with others.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Users can sign up and log in into account.
* Users can upload profile picture using the camera.
* Users can create jobs.
* Users can see a list of jobs that other people posted.
* Users can search for jobs and filter them (Ex: Location, Money, etc).
* Users can request to complete jobs and users can approve of them or not.
* Once approved of job, user can notify when they have finished a job.

**Optional Nice-to-have (Stretch) Stories**
* Users can pay person via cash or online.
* Users can look for jobs using Google Maps.
* Users can add and remove friends.
* Users can recieve notifications.
* Users can approve specific user when creating a job post.
* Users can change account settings.

### 2. Screen Archetypes

* Login Activity
   * User can log into their account.
   * If user does not have an account, it can sign up for one.
* Detail/Home Activity
   * User can see job posts.
   * User can submit request to complete job.
* Creation (Fragment)
   * User can compose a job post.
* Profile (Fragment)
   * User can view public account information.
   * User can view job tasks that they requested and approve users.
* MyJobs (Fragment)
   * User can view job tasks that they want to do and check approval status.

### 3. Navigation

**Tab Navigation** (Tab to Screen)
Fragments used to navigate between activites.

* Home
* Search
* Create
* MyTasks
* Profile

**Flow Navigation** (Screen to Screen)
* LoginActivity
   * Go from login screen to Home Screen
* MainActivity
   * HomeFragment: Fetch query posts
   * ComposeFragment: Create job and then go to home timeline.
   * TaskFragment: User can view jobs they have requested and get approved or denied.
   * SearchFragment: Job queries based on search and filters.
   * ProfileFragment: User profile.
*JobDetailsActivity
  *User can view job details.
*RequestActivity
  * User can view the request for the job they have maded and they can approve user.
* SignUpActivity
   * Direct user to sign up and appear in HomeActivity Screen


## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="https://github.com/patrick141/QuickCash/raw/master/IMG_7888.jpg" width=600>

### [BONUS] Digital Wireframes & Mockups
https://www.figma.com/file/QbfAQARWlZ9yfjCAmeGdxs/QuickCash?node-id=0%3A1

### [BONUS] Interactive Prototype
Coming Soon!
https://www.figma.com/embed?embed_host=share&url=https%3A%2F%2Fwww.figma.com%2Ffile%2FQbfAQARWlZ9yfjCAmeGdxs%2FQuickCash%3Fnode-id%3D0%253A1&chrome=DOCUMENTATION

## Schema
### Models
USER
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user (default field) |
   | username    | String     | User's profile picture |
   | password     | String     | User's profile picture |
   | description      | String   | User's profile description |
   | createdAt     | DateTime | date when user is created (default field) |
   | updatedAt     | DateTime | date when user is last updated (default field) |
  | myJobs | Array | This is the user's jobs that they created|
   | completedJobs | Array | This is the user's jobs that they were assigned and have completed.|

JOB
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | A unique id for the user post (default field) |
   | _user_        | Pointer to User| User who posted the job |
   | name | String | The job name (ex: Tutor Needed)  |
   | image     | File     | Picture associated with job(if provided) |
   | description      | String   | A small description for the job|
   | price | Number | The price amount the job creator is willing to pay |
   | address | String | Address of the Job|
   | location      | GeoPoint | Coordinate Points of job|
   | createdAt     | DateTime | date when post is created (default field) |
   | dateOfJob | DateTime | Job date and time |
   | requests | Array | Array of requests for the job |
   | assignedUser | Pointer to User | id for the user approved for job |
   |isTaken | Boolean | Checks if|
   |isFinished |Boolean| Checks to see if the job has been completed|


NOTIFICATION
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | Unique id for the notification (default field) |
   | messenger   | Pointer to User| Notification author |
   | recipient   | Pointer to User| User who will recieve the notification |
   |messenge| String | The notification's message|
   | referJob | Pointer to Job| Job that the notification is refering to|
   | referRequest| Pointer to Request| Request the notification is refering to|
   | description      | String   | Job description by author |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |


REQUEST
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | Unique id for the user post (default field) |
   | _user_        | Pointer to User| Job post author |
   | jobPost | Pointer to Job| Job that the request is for |
   | description      | String   | Job description by author |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |


### Networking
   - Home Feed Screen
      - (Read/GET) Query all job posts where user is author.
      - (Read/GET) Query all job posts where user hasn't been assigned.
      - (Create/POST) Create a job post.
      - (Delete) Delete existing job post.
      - (Create/POST) Create a new request on a job post.
      - (Delete) Delete existing job request.
   - Create Post Screen
      - (Create/POST) Create a new job post
   - Profile Screen
      - (Read/GET) Query logged in user object
      - (Update/PUT) Update user profile image
#### [OPTIONAL:] Existing API Endpoints
- Google Maps SDK & API
- Google Places API
- Parse Database & API
