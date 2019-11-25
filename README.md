# CEBP

Stock exchange project for university for learning and demonstrating concurrent and event based programming skills.  

# Documentation

###Architecture of the project

In order to model the stock market we've split the application into 2 separate programs. One program that will be running 
continuously on the server and another that will be distributed to all clients.

Each client can use their program to connect to the server either as a stock buyer or a stock seller. The communication
between clients and server is provided through Java web sockets. Each client will connect to the server via port 5000. It has a websocket hence via the HTTP protocol a free socket on the server side will be allocated fot that user in order to access the stock market. 

Having split the program in this way would mean that clients would never be able to communicate with one another directly, allowing us to focus on the concurrency and consistency problems only on the server's end, more exactly, each client represents one thread on the server while the client has a graphical interface that controls its thread actions. The server works around a <WallStreet> type object, which contains 3 lists of <Transactions> objects and methods to operate on said lists safely. The 3 lists represent the sell offers, the buy requests and the comleted transactions. For instance if someone were to place a sale offer, the sell offers list on the server would be updated with this addition. If someone else would want to place a purchase request afterwards, the request would first be verified to see whether it matches the characteristics of a sale offer already available or not. If it does match a sale offer, the said sale offer will be removed from the sell offers list, nothing will be added to the buy requests, and the terminated transactions list would have this transaction added. If the purchase request would not match the characteristics of any sell offer present so far on the server, it would simply be added to the list of requests so that other users can see it. These lists that are used to store all the data are customized so as to permit concurrent and consistent access with the use of 2 Mutexes. A read and a write Mutex helps to prevent unpredictability and inconsistency of the data by only allowing one user at a time to write data, while multiple reads can be performed simultaneously(notice that a write will prevent reading from happening). This will ensure that no matter how many users make requests at the same time, the server will only allow one, the first, request it gets to pass through and work with the data, while the others have to wait in queue. The Mutex used for the protected lists is the one from sun.awt. When a sale offer matches in price with a buy request, an object of type Transaction will be added the transaction list and the amount of stocks present in the buy request will be deduced from the sale offer. Likewise in the case of a buy request matching a sale offer.

The producers of all the data to be added to the aforementioned structure will be the clients, the people who will be using the front program to connect to the server. From the server's persepective a client (Abstract class) can be one of two things, a buyer or a seller, each modelled by his own class. In both cases, the client will be sending the information through the socket that the server's listening port will allocate. This socket will uniquely identify the client. The clients will make their offers and requests, send them forward and Wallstreet will take care of managing them. 

From the point of view of the customer, things are very simple. The client is provided with an easy to use interface in which he'll be introducing his input. In short, all that this other program is in charge of is making the connection to the server on the right address and port, and then sending through this port the information that the user provides. The user has an initial screen in which he can choose his type of client (buyer or seller). After that he'll be forwarded to a screen in which he can directly say how many stocks he wants to buy/sell and for how much. The same screen provides a dropdown menu with 5 options. The user can choose from this dropdown to see his offers, his transactions, all transactions, all sell offers and all buy requests. Every action of the user gets interpreted and sent to the server via the socket, and if the user made a request of any kind, his request will be subject to the rules imposed by the structure (the lists of data) protected by the Mutex. The client will also make requests to read information from the server, so as to be able to display it for the user. These requests to read information will abide by the same rules as the requests to write information.

The project was written in Eclipse and Intellij using Java 8 and has been transformed into a Maven project in the meantime. The graphical user interface is provided by JavaFX. Some style was added with CSS to the default FX appearance as well as some positioning tweaks. The core of the project is composed of Java's websockets and sun.awt's Mutex.

In terms of user management the algorithm used is pretty straightforward from the point of view of development. Considering that each thread has its associated socket created by the server resulted in the simple idea to use the socket for differentiating between users. Hence the port of the connection is the unique ID of the user. As a consequence, the state of the user actions cannot be saved since one socket may be allocated to the first user that logs in after the one that logs out now providing the users with inconsistencies. This project is about concurrency problems so a better user management solution was not found necessary so when an customer disconnects it is interpreted as deleting the account and all the transactions left unfinished.

For the user to be able to control its thread a simple application layer protocol was developed. The protocol relies on the TCP transfer protocol because no data is sent unnecessary. The following represent the description of the protocol:

#### Server
* Waits for the client to send its type
* Each client thread is in idle mode waiting for instructions. Depending on the instruction received it will 
* Transactions, Sell offers, Buy offers, All offers, My offers are used to get the required list. When the server receives one of this instruction it sends the size of the list followed by the result of toString method applied to each element from the list(one per line)
* offer instructs the server that an offer is comming so it will read, in this order, the name(ID) of the user, the price and amount of transaction. The name may be followed by index + the index of the transaction that needs to be updated
* end or a null input will allow the server to close the socket associated to that user, the input and output streams used for communication, the list of transaction of that user and all unfinished transactions

#### Client
* Sends its type to the server right after it connects(connection starts only after the user chooses the type)
* When the user presses the item in the menu for any list it will send the corresponding instruction
* When start transaction is pressed the ID of the user, the amount and price introduced are sent. If the user selected an entry in My Transactions beforehand the ID will be appended with index + the index of that transaction and in this case it will be updated rather than added
* When the user closes the app it automatically sends end message to the server

### Concurrency problems