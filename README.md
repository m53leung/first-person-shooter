# first-person-shooter
The ray casting algorithm used to render the walls of the game was implemented in: FirstPersonShooter/src/GameMain.java within the gameDraw function. The code from line 916-953 renders the walls of the world by creating "drawable" objects and adding them to the arraylist "z-buffer". At the end of the gameDraw function, a simple for loop draws all of the drawable objects in z-buffer to the canvas. The rayTrace function used in gameDraw can be found at line 796.

Dijkstra's algorithm was implemented in: FirstPersonShooter/src/Map.java within the dijkstra method at line 376.

If you wish to play the game, please download and run GunnerZ_Final.jar.
