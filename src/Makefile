all: driver

# ==========================
#  Directory specification.
# ==========================

COMPILER := javac
FILE_EXT := .java
COMPILER_FLAGS := -ea
OBJ_DIR := build

# ==========================
#  Compile rule for class
#  and source files.
# ==========================

driver:
	mkdir -p $(OBJ_DIR)
	javac Driver.java */*.java -d $(OBJ_DIR) -cp $(OBJ_DIR)

run: driver
	java -cp $(OBJ_DIR) Driver

# ==========================
#  Rules for laziness (can
#  be removed).
# ==========================
c: clean
cc: clean all

# ==========================
#  Cleaner.
# ==========================

clean:
	rm -rf $(OBJ_DIR)/*
