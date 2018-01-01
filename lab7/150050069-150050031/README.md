# Twitter SocMed

Team
- Harshith Goka - 150050069
- Akash Trehan - 150050031

## Guidelines 
1. You can find the backup of our server at sql/backup1.sql, if that doesn't work then try using create.sql and data.sql, even if that doesn't work, try using sql/backup.sql
2. server folder contains the whole project
3. client folder contains the whole android-studio project
4. Server details can be changed in the file Constants.java
5. Also added required jar to run the server

## Features 
1. Automatic Infinite scrolling on both directions for only Feed.
2. If you want to refresh posts you can swipe down to refresh in any screen.
3. We have added add comments inline
4. We cached the images on Disk to save bandwidth and is shared across posts
5. We have persistent cookies stored in SharedPrefs
6. In search tab, you get the posts directly when you select a user.
7. You can follow/unfollow with no ambiguity.
8. Enabled compression of images before sending then to the server.
9. And also images are not sent with posts, only an imageid, which is then queried on another servlet to get multiple images in parallel.
10. All Network requests are made using a Background Service and using the Handler-Thread-Looper model used by android


## Citations
Used to generate Random Unique ID for images
http://blog.joevandyk.com/2013/04/18/generating-random-ids-with-postgresql/

Fair idea of CookieStore from 
https://stackoverflow.com/questions/12349266/how-do-i-persist-cookies-when-using-httpurlconnection

