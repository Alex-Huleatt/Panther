Panther
=======
Notes on the use of the pathfinder.
Required imports:
pathfinding.Panther needed for A*.
util.Stack needed for paths returned by Panther.
pathfinding.MBugger needed for bugging.


1. Add obstacles use Panther.addObstacle(Point p). Duplicates are fine, it automagically stores and checks.
2. Flush the obstacle buffer when desired. The more obstacles that have been added, the longer this will take.
3. Remove old edges. The time for this is a function of the number of edges in the graph multiplied by the number of obstacles that have been added since this was last called.
4. Add new edges. The time for this is based on the number of waypoints in the map currently multiplied by the number of recently added waypoints (<= 4 * new obstacles).
5. Pathfind.

The path returned is currently a Stack<Point>. If need be, we can shove this into an array.

