package hu.bme.mit.ftsrg.hungryelephant;

import hu.bme.mit.ftsrg.hungryelephant.handler.RequestHandler;
import hu.bme.mit.ftsrg.hungryelephant.model.DatabaseModel;
import hu.bme.mit.ftsrg.hungryelephant.model.Food;
import hu.bme.mit.ftsrg.hungryelephant.model.Restaurant;
import hu.bme.mit.ftsrg.hungryelephant.model.User;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class HungryElephantApplication {
	protected final int port;
	private final File configXML;
	private final PrintStream out;
	private final PrintStream err;
	private DatabaseModel model;

	public HungryElephantApplication(int port, File configXML, PrintStream out,
			PrintStream err) {
		this.port = port;
		this.configXML = configXML;
		this.out = out;
		this.err = err;
	}
	
	public DatabaseModel getModel(){
		return model;
	}
	
	private void run() {
		out.println("===== HungryElephant Server =====");
		out.println("Run requested");
		out.println("  Desired port: " + port);
		out.println("  Configuration file: " + configXML);

		try {
			out.println("== Parsing configuration file ==");
			parseConfig();

			out.println("== Starting server ==");
			startServer();
		} catch (HungryElephantException e) {
			err.println("Exception: " + e.getMessage());

			if (e.getCause() != null) {
				e.getCause().printStackTrace(err);
			}
		}
	}

	private void parseConfig() throws HungryElephantException {
		try {
			model = new DatabaseModel();

			// read and normalise XML
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = builder.parse(configXML);
			doc.getDocumentElement().normalize();

			// validate root element
			Element rootElement = doc.getDocumentElement();
			if ("config".equals(rootElement.getNodeName())) {
				parseUsers(rootElement);
				parseRestaurants(rootElement);
				parsePages(rootElement);
			} else {
				throw new HungryElephantException(
						"Configuration file: the root element should be <config>");
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new HungryElephantException(
					"Unexpected exception occured when parsing"
							+ " the configuration file (see details)", e);
		}
	}

	private void parseUsers(Element rootElement) throws HungryElephantException {
		NodeList elements = rootElement.getElementsByTagName("user");

		if (elements.getLength() == 0) {
			throw new HungryElephantException(
					"Configuration file: at least one user is required");
		}

		// get data for each element
		for (int i = 0; i < elements.getLength(); i++) {
			Element element = (Element) elements.item(i);

			if (!element.getParentNode().getNodeName().equals("users")) {
				continue;
			}

			Element usernameElement = (Element) element.getElementsByTagName(
					"username").item(0);
			Element passwordElement = (Element) element.getElementsByTagName(
					"password").item(0);

			// no missing data
			if (usernameElement == null || passwordElement == null) {
				throw new HungryElephantException(
						"Configuration file: the username and password must be specified for each user");
			}

			// create User object
			User user = new User(usernameElement.getTextContent().trim(),
					passwordElement.getTextContent().trim());

			// save to the model
			model.users().put(user.getId(), user);
		}
	}

	private void parseRestaurants(Element rootElement)
			throws HungryElephantException {
		NodeList elements = rootElement.getElementsByTagName("restaurant");

		if (elements.getLength() == 0) {
			return;
		}

		// get data for each element
		for (int i = 0; i < elements.getLength(); i++) {
			Element element = (Element) elements.item(i);

			Element nameElement = (Element) element
					.getElementsByTagName("name").item(0);
			Element addressElement = (Element) element.getElementsByTagName(
					"address").item(0);
			Element menuElement = (Element) element
					.getElementsByTagName("menu").item(0);
			Element dispatchersElement = (Element) element
					.getElementsByTagName("dispatchers").item(0);

			// no missing data
			if (nameElement == null || addressElement == null
					|| menuElement == null || dispatchersElement == null) {
				throw new HungryElephantException(
						"Configuration file: the name, address, menu and the dispatchers must be specified for each restaurant");
			}

			try {
				// create Restaurant object
				Restaurant restaurant = new Restaurant(nameElement
						.getTextContent().trim(), addressElement
						.getTextContent().trim());

				// parse menu
				NodeList foodElements = menuElement
						.getElementsByTagName("food");
				for (int j = 0; j < foodElements.getLength(); j++) {
					Element foodElement = (Element) foodElements.item(j);
					String foodName = foodElement.getTextContent().trim();
					int foodPrice;
					try {
						foodPrice = Integer.parseInt(foodElement
								.getAttribute("price"));
					} catch (NumberFormatException e) {
						throw new HungryElephantException("Invalid food price",
								e);
					}

					Food food = model.foods().get(foodName);
					if (food == null) {
						food = new Food(foodName);
						model.foods().put(food.getId(), food);
					}

					restaurant.menu().put(food, foodPrice);
				}

				// parse dispatchers
				NodeList userElements = dispatchersElement
						.getElementsByTagName("user");
				for (int j = 0; j < userElements.getLength(); j++) {
					Element userElement = (Element) userElements.item(j);
					String username = userElement.getTextContent().trim();

					User user = model.users().get(username);
					if (user == null) {
						throw new HungryElephantException(
								"Unknown user cannot be dispatcher: "
										+ username);
					}

					restaurant.dispatchers().add(user);
				}

				// save to the model
				model.restaurants().put(restaurant.getId(), restaurant);
			} catch (Exception e) {
				throw new HungryElephantException(
						"Configuration file: invalid data (see details)", e);

			}
		}
	}

	private void parsePages(Element rootElement) throws HungryElephantException {
		NodeList elements = rootElement.getElementsByTagName("page");

		if (elements.getLength() == 0) {
			return;
		}

		// get data for each element
		for (int i = 0; i < elements.getLength(); i++) {
			Element element = (Element) elements.item(i);

			String name = element.getAttribute("name");
			String content = element.getTextContent().trim();

			// save to the model
			model.pages().put(name, content);
		}
	}

	private void startServer() throws HungryElephantException {
		// open socket
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			out.println("Socket created");
			ExecutorService threadPool = Executors.newCachedThreadPool();

			while (true) {
				// accept connections from the clients
				try {
					Socket socket = serverSocket.accept();
					out.println("Client connected: " + socket.getInetAddress()
							+ ":" + socket.getPort());

					// handle request
					threadPool.execute(new RequestHandler(socket, model, out,
							err));
				} catch (IOException e) {
					throw new HungryElephantException(
							"Unexpected exception occured when creating a socket (see details)",
							e);
				}
			}
		} catch (IOException e) {
			throw new HungryElephantException(
					"Unexpected exception occured when starting the server socket (see details)",
					e);
		}
	}

	public static void main(String[] args) {
		// parse command line arguments
		int port = -1;
		File configXML = null;
		List<String> errors = new ArrayList<>();

		if (args.length == 2) {
			// port
			try {
				port = Integer.parseInt(args[0]);

				if (port <= 0 || port >= 65536) {
					errors.add("Invalid port: " + port);
				}
			} catch (NumberFormatException e) {
				errors.add("Not a number: " + port);
			}

			// configuration file
			configXML = new File(args[1]);

			if (!configXML.exists()) {
				errors.add("The configuration file does not exists");
			} else if (!configXML.canRead()) {
				errors.add("The configuration file cannot be read");
			}
		} else {
			errors.add("Missing parameters");
		}
		
		HungryElephantApplication hea=null;
		
		if (errors.size() > 0) {
			// print errors
			System.err.println("An error occured");

			for (String error : errors) {
				System.err.println("  " + error);
			}

			System.err
					.println("Usage: java -jar HungryElephant.jar <port> <configxml>");
		} else {
			// start application
			try {
				new HungryElephantApplication(port, configXML, System.out,
						System.err).run();
			} catch (Exception e) {
				System.err.println("Uncaught exception");
				System.err.println("  Class: " + e.getClass());
				System.err.println("  Message: " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		HungryElephantApplicationControl heac = new HungryElephantApplicationControl(hea);
		
		try {
			
			ObjectName name = null;
			name = new ObjectName("hu.bme.mit.ftsrg.hungryelephant:type=control");
		
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			
			mbs.registerMBean(heac, name);


		} catch (Exception e) {
			/*
			 * Here we may receive:
			 *   InstanceAlreadyExistsException
			 *   MBeanRegistrationException
			 *   NotCompliantMBeanException
			 *   NullPointerException
			 *   MalformedObjectNameException
			 */
			e.printStackTrace();
			System.exit(2);
		}
	}
}
