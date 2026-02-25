SRC_DIR=src
JAVA_DIR=main/java
CLASSPATH=target/classes/src
%: $(SRC_DIR)/$(JAVA_DIR)/steps/%.java
	mkdir -p $(CLASSPATH) && cd $(SRC_DIR) && javac -d ../$(CLASSPATH) $(JAVA_DIR)/steps/$@.java