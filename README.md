# CEBP

Stock exchange project for university for learning and demonstrating concurrent and event based programming skills.  

# Documentation

###Architecture of the project

In order to model the stock market we've split the application into 2 separate programs. One program that will be run
ing continuously on the server and another that will be distributed to all clients.
Each client can use their program to connect to the server either as a stock buyer or a stock seller. The communicatio
between clients and the server is provided through Java web sockets. Each client will connect to a free socket on the
server in order to access the stock market. Having split the program in this way would mean that clients would never
be able to communicate with one another directly, allowing us to focus on the concurrency and consistency problem
nly on the server's end. The server works around a <WallStreet> type object, which contains 3 lists of <Transactions
objects and methods to operate on said lists safely. The 3 lists represent the sell offers, the buy requests and the com
leted transactions. For instance if someone were to place a sale offer, the sell offers list on the server would be updat
d with this addition. If someone else would then want to place a purchase request, the request would first be verified
o see whether does not match the characteristics of a sale offer already available. If it does match a sale offer, the sai
sale offer will be removed from the sell offers list, nothing will be added to the buy requests, and the terminated tra
sactions list would have this transaction added. If the purchase request would not match the characteristics of any sal
offer present so far on the server, it would simply be added to the list of requests so that other users can see it.
These lists that are used to store all the data are customized so as to permit concurrent and consistent access with the
use of 2 Mutexes. A read and a write Mutex helps to prevent unpredictibility and inconsistency of the data by only al
owing one user at a time to interrogate the data. This will ensure that no matter how many users make requests at the
same time, the server will only allow the first request it gets to pass through and work with the data, while the others
have to wait in queue. The Mutex used for the protected lists is the one from sun.awt.
When a sale offer matches in price with a buy request, an object of type Transaction will be added the transaction lis
, and the amount of stocks present in the buy request will be deduced from the sale offer. Likewise in the case of a b
y request matching a sale offer.
The producers of all the data to be added to the aforementioned structure will be the clients, the people who will be u
ing the other program to connect to the server. From the server's persepective a client (Abstract class) can be one of t
o things, a buyer or a seller, each modelled by his own class. In both cases, the client will be sending the information
through the socket that the server's listening port will allocate. This socket will uniquely identify the client. The clien
s will make their offers and requests, send them forward and Wallstreet will take care of managing them.
From the point of view of the customer, things are very simple. The client is provided with an easy to use interface i
which he'll be introducing his input. In short, all that this other program is in charge of is making the connection to t
e server on the right address and port, and then sending through this port the information that the user provides.
The user has an initial screen in which he can choose his type of client (buyer or seller). After that he'll be forwarded
to a screen in which he can directly say how many stocks he wants to buy/sell and for how much. The same screen p
ovides a dropdown menu with 5 options. The user can choose from this dropdown to see his offers, his transactions,
ll transactions, all sell offers and all buy requests. Every action of the user gets interpreted and sent to the server via t
e socket, and if the user made a request of any kind, his request will be subject to the rules imposed by the structure (
he lists of data) protected by the Mutex. The client will also make requests to read information from the server, so as
to be able to display it for the user. These requests to read information will abide by the same rules as the requests to
write information.
The project was written in Eclipse using Java 8 and has been transformed into a Maven project in the meantime. The
graphical user interface is provided by JavaFX. Some style was added with css to the default FX appearance as well
s some positioning tweaks. The core of the project is composed of Java's websockets and sun.awt's Mutex.
