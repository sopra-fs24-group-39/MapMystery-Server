[1mdiff --git a/.gitignore b/.gitignore[m
[1mindex 592368b..3165022 100644[m
[1m--- a/.gitignore[m
[1m+++ b/.gitignore[m
[36m@@ -38,6 +38,7 @@[m [mhs_err_pid*[m
 [m
 # don't track the local.properties -> contains secrets[m
 local.properties[m
[32m+[m[32mcontributions.md[m
 [m
 .vscode[m
 [m
[1mdiff --git a/.gitignore.save b/.gitignore.save[m
[1mindex 908ec4f..8f0dcbd 100644[m
[1m--- a/.gitignore.save[m
[1m+++ b/.gitignore.save[m
[36m@@ -38,6 +38,7 @@[m [mhs_err_pid*[m
 [m
 # don't track the local.properties -> contains secrets[m
 local.properties[m
[32m+[m[32mcontributions.md[m
 [m
 .vscode[m
 [m
[1mdiff --git a/betatest.md b/betatest.md[m
[1mdeleted file mode 100644[m
[1mindex abb1259..0000000[m
[1m--- a/betatest.md[m
[1m+++ /dev/null[m
[36m@@ -1,21 +0,0 @@[m
[31m-# Beta Test: Project of Group 40:[m
[31m-https://sopra-fs24-group-40-client.oa.r.appspot.com/login[m
[31m-[m
[31m-## Thigns that are good:[m
[31m-Chat[m
[31m-[m
[31m-## Bugs:[m
[31m-I created a game with two players. Then with one player I searched for the other player, which kicked me out of the lobby with player2 and player1 finished the game and clicked play again. But now neither of the two players can join or create a new lobby because it say it is still in ENDGAME[m
[31m-[m
[31m-When the server resets and a player did not log out. They are still in the overview screen but do not have an account which leads to errors. When trying to log out nothing happens[m
[31m-[m
[31m-## Usability:[m
[31m-"Register", "Login", and "Back to" button have same color but "Register" button chagnes place - not that intuitive[m
[31m-[m
[31m-Chaning the name does not seem to work. I enter the name, I press enter the field closes and the name does not change[m
[31m-[m
[31m-Chaning the avatar does not work. I can click on the change avatar button, I can click on the choose button but nothing happens. [m
[31m-[m
[31m-Open lobbies are only visible if I manually refresh the page[m
[31m-[m
[31m-If all players submitted an answer they still have to wait for the timer to run out[m
[1mdiff --git a/contributions.md b/contributions.md[m
[1mdeleted file mode 100644[m
[1mindex a306dff..0000000[m
[1m--- a/contributions.md[m
[1m+++ /dev/null[m
[36m@@ -1,222 +0,0 @@[m
[31m-# Contributions:[m
[31m-## M3 05.04.2024 - 12.04.2024:[m
[31m-### Nils:[m
[31m-#### What did I do last week?[m
[31m-- Created the buttons [#9](https://github.com/sopra-fs24-group-39/MapMystery-client/issues/9#issue-2051971058)[m
[31m-- Navbar skeleton [#13](https://github.com/sopra-fs24-group-39/MapMystery-client/issues/13#issue-2051969558)[m
[31m-- An outline of the main page [#13](https://github.com/sopra-fs24-group-39/MapMystery-client/issues/13#issue-2051969558)[m
[31m-#### What will I do this week?[m
[31m-- Create the frontend draft for Login and Registration[m
[31m-- Connect the Login/Registry to the backend[m
[31m-#### What are the obstacles to progress?[m
[31m-- Staying as close to the design draft as possible[m
[31m-- Getting along with all the CSS design[m
[31m-- Trying to keep the code clean, readable and easy to understand[m
[31m-### Samuel:[m
[31m-#### What did I do last week?[m
[31m-- created Email account https://github.com/sopra-fs24-group-39/MapMystery-Server/issues/38#issue-2200489858[m
[31m-- JAVA Email sender implementation https://github.com/sopra-fs24-group-39/MapMystery-Server/issues/38#issue-2200489858[m
[31m-- Verification email + creation email sent https://github.com/sopra-fs24-group-39/MapMystery-Server/issues/41[m
[31m-- Verification link -> database update[m
[31m-- Secure password requirements [m
[31m-#### What will I do this week?[m
[31m-- API Integration for Data Fetching https://github.com/sopra-fs24-group-39/MapMystery-Server/issues/5[m
[31m-- Get mapping [m
[31m-- Game Object[m
[31m-- Tally Points for players[m
[31m-- Function to get coordinates[m
[31m-[m
[31m-[m
[31m-#### What are the obstacles to progress?[m
[31m-### David:[m
[31m-#### What did I do last week?[m
[31m-this issue (https://github.com/sopra-fs24-group-39/MapMystery-Server/issues/92)[m
[31m-that issue (https://github.com/sopra-fs24-group-39/MapMystery-Server/issues/93)[m
[31m-#### What will I do this week?[m
[31m-REST API endpoint for players who want to leave a lobby[m
[31m-Desgining and implementing an exception system for the backend, such that people can actually understand why[m
[31m-a request might fail.[m
[31m-#### What have I done in total?[m
[31m-[m
[31m- copmleted for 05.04.2024[m
[31m-(https://github.com/sopra-fs24-group-39/MapMystery-Server/issues/23)[m
[31m-(https://github.com/sopra-fs24-group-39/MapMystery-Server/issues/24)[m
[31m-[m
[31m-copmleted for  12.04.2024[m
[31m-(https://github.com/sopra-fs24-group-39/MapMystery-Server/issues/26)[m
[31m-(https://github.com/sopra-fs24-group-39/MapMystery-Server/issues/27)[m
[31m-[m
[31m-copmleted for 19.04.2024[m
[31m-(https://github.com/sopra-fs24-group-39/MapMystery-Server/issues/63)[m
[31m-(https://github.com/sopra-fs24-group-39/MapMystery-Server/issues/65)[m
[31m-[m
[31m-copmleted for 26.04.2024[m
[31m-(https://github.com/sopra-fs24-group-39/MapMystery-Server/issues/92)[m
[31m-(https://github.com/sopra-fs24-group-39/MapMystery-Server/issues/93)[m
[31m-[m
[31m-copmleted for 03.05.2024[m
[31m-(https://github.com/sopra-fs24-group-39/MapMystery-Server/issues/111)[m
[31m-(https://github.com/sopra-fs24-group-39/MapMystery-Server/issues/112)[m
[31m-[m
[31m-[m
[31m-#### What are the obstacles to progress?[m
[31m-### Joshua:[m
[31m-#### What did I do last week?[m
[31m-- Profile Updating Endpoint  (https://github.com/sopra-fs24-group-39/MapMystery-Server/issues/30)[m
[31m-- Rankings Visibility Toggle (https://github.com/sopra-fs24-group-39/MapMystery-Server/issues/32)[m
[31m-- Fundamentals for the Google Street View API Implementation[m
[31m-#### What will I do this week?[m
[31m-- Integrate & configure the Google API calls for the Frontend    [m
[31m-- Uniqueness check for email and username when updating / creating a profile[m
[31m-#### What are the obstacles to progress?[m
[31m-- Deciding on which functionalities should be carried out by the server or by the client[m
[31m-### Tim:[m
[31m-#### What did I do last week?[m
[31m-(issues: #10, #14, #15, #16, #17)[m
[31m-- Components [#10](https://github.com/sopra-fs24-group-39/MapMystery-client/issues/10)[m
[31m-    - created Title component[m
[31m-    - created Logo component[m
[31m-    - created base element component[m
[31m-    - added generated profile pictures based on username[m
[31m-    - changed things navigation bar (added Profile Picture and Logo)[m
[31m-    - created searchbar component[m
[31m-    - created tab selection component[m
[31m-    - added background to base container[m
[31m-- Settings Page [#14](https://github.com/sopra-fs24-group-39/MapMystery-client/issues/14)[m
[31m-    - created and populated settings page[m
[31m-    - added content to the base element in settings[m
[31m-[m
[31m-- Landing Page [#17](https://github.com/sopra-fs24-group-39/MapMystery-client/issues/17)[m
[31m-    - created and populated landing page[m
[31m-[m
[31m-- Rankings Page [#16](https://github.com/sopra-fs24-group-39/MapMystery-client/issues/16)[m
[31m-    - created and populated rankings page[m
[31m-    - add content to the base element in rankings[m
[31m-[m
[31m-- Friends Page [#15](https://github.com/sopra-fs24-group-39/MapMystery-client/issues/15)[m
[31m-    - created and populated friends page[m
[31m-    - add content to the base element in friends[m
[31m-#### What will I do this week?[m
[31m-- Components for the Game modes[m
[31m-- create a chat component in various forms (open, closed, button).[m
[31m-- create a dropdown menu and integrate it with the profile picture so that it appears when pressed.[m
[31m-- fix small things like cursor = pointer in navbar, buttons animation on click, not hover[m
[31m-#### What are the obstacles to p