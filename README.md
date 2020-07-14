# #1: QuickCash

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Need to make money fast? QuickCash is an app that allows people to find easy-quick money jobs. Users can request specific people to help complete tasks and pay them afterwards. Examples include but not limited to (Car-wash, dog-walking, move heavy items, etc). 

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
* Users can post job tasks.
* Users can see a list of tasks that other people posted.
* Users can search for tasks and filter them (Ex: Location, Money, etc).
* Users can request to complete tasks and users can approve of them or not.

**Optional Nice-to-have (Stretch) Stories**
* Users can pay person via cash or online.
* Users can add and remove friends.
* Users can have Endless Scrolling Feature.
* Users can comment on posts and interact with users.
* Users can recieve notifications.

### 2. Screen Archetypes

* Login Activity
   * User can log into their account.
   * If user does not have an account, it can sign up for one.
* Detail/Home Activity
   * User can see posts of job tasks.
   * User can submit request to complete job task.
* Creation (Fragment)
   * User can compose job task.
* Profile (Fragment)
   * User can view public account information.
   * User can view job tasks that they requested and approve users.
* MyTasks (Fragment)
   * User can view job tasks that they want to do and check approval status.
* Settings (Fragment)
   * User can view private account information. 
   * User can change account settings. 

### 3. Navigation (NEED TO UPDATE)

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
[This section will be completed in Unit 9]
### Models
REQUEST
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user post (default field) |
   | _user_        | Pointer to User| image author |
   | picture       | File     | image that user posts |
   | description      | String   | job description by author |
   | cashAmount | Number | amount author is willing to pay |
   | commentsCount | Number   | number of comments that has been posted to an image |
   | location      | Location/Float | Captures location of request |  
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
   | requested | Boolean | if job has been approved. |
   | approvedUser | Pointer to User | id for the user approved for task |
   
USER
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user (default field) |
   | profilePic     | File     | user's profile picture |
   | description      | String   | job description by author |
   | commentsCount | Number   | number of comments that has been posted to an image |
   | location      | Location/Float | Captures location of request |  
   | createdAt     | DateTime | date when user is created (default field) |
   | updatedAt     | DateTime | date when user is last updated (default field) |
   | myRequests | Array | users' request|
   | requested | Boolean | if job has been approved. |
   | approvedUser | Pointer to User | id for the user approved for task |

### Networking
   - Home Feed Screen
      - (Read/GET) Query all posts where user is author
      - (Create/POST) Create a new like on a post
      - (Delete) Delete existing like
      - (Create/POST) Create a new comment on a post
      - (Delete) Delete existing comment
   - Create Post Screen
      - (Create/POST) Create a new post object
   - Profile Screen
      - (Read/GET) Query logged in user object
      - (Update/PUT) Update user profile image
#### [OPTIONAL:] Existing API Endpoints
- Google Maps SDK & API
- Parse Database & API
