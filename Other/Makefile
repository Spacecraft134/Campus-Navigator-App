WebApp.class: WebApp.java
	javac -cp .:../junit5.jar WebApp.java

Backend.class: Backend.java
	javac -cp .:../junit5.jar Backend.java

Frontend.class: Frontend.java
	javac -cp .:../junit5.jar Frontend.java

BaseGraph.class: BaseGraph.java
	javac -cp .:../junit5.jar BaseGraph.java

DijkstraGraph.class: DijkstraGraph.java
	javac -cp .:../junit5.jar DijkstraGraph.java

GraphADT.class: GraphADT.java
	javac -cp .:../junit5.jar GraphADT.java

HashtableMap.class: HashtableMap.java
	javac -cp .:../junit5.jar HashtableMap.java

MapADT.class: MapADT.java
	javac -cp .:../junit5.jar MapADT.java

runServer: WebApp.class Backend.class Frontend.class \
         BaseGraph.class DijkstraGraph.class \
         GraphADT.class HashtableMap.class MapADT.class
	 sudo java WebApp 80

FrontendTests.class: FrontendTests.java
	javac -cp .:../junit5.jar FrontendTests.java

BackendTests.class: BackendTests.java
	javac -cp .:../junit5.jar BackendTests.java

runTests: FrontendTests.class BackendTests.class
	java -jar ../junit5.jar -cp . -c FrontendTests
	java -jar ../junit5.jar -cp . -c BackendTests


clean:
	rm -f *.class
