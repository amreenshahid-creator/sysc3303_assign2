# Battle Royale Simulation
This is a simple real-time battle royale style system using UDP packets and sockets

## Java File Descriptions
GameState.java: This class is responsible for holding the state of the game including players and their loot boxes

Player.java: This class is responsible for holding a single players state within a game including their IDs, health, 
position and display name.

LootBox.java: this class is responsible for holding a single loot box containing items that users can pick up.

Client.java: This class is responsible for allowing users to join the game, and enter commands such as JOIN, MOVE,
PICKUP, STATE and QUIT and sends this information to the Host.

Host.Java: This class is responsible for the communication between the client and the server by receiving information
from the client and sending it off the server without any modification.

Server.java: This class is responsible for responding to the clients requests and responding accordingly with a messages.

## Diagram Descriptions
BattleRoyale_classDiagram.jpg: The UML class diagram showing the relationships between all the classes.

BattleRoyale_sequence.jpg: The sequence diagram showing the interaction between all the classes during a
successful PICKUP command.


## Set Up Instructions
1. Open folder in IntelliJ
2. Run the classes in this order
- Server.java
- Host.java
- Client.java
3. The client terminal allows you to type in a player name, enter a name and enter commands
4. Observe the three terminals to see their interactions 


