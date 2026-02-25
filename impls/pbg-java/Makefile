SRC_DIR=src
JAVA_DIR=main/java
%: $(SRC_DIR)/$(JAVA_DIR)/steps/%.java
	mkdir -p target/classes/mal && cd $(SRC_DIR) && javac -d ../target/classes/mal $(JAVA_DIR)/steps/$@.java