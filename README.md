vector-test [ ![Download](https://api.bintray.com/packages/zelkatani/vector-test/vector-test/images/download.svg) ](https://bintray.com/zelkatani/vector-test/vector-test/_latestVersion)
=====

vector-test is a library for programmatically generating test vector files for [Logisim](https://github.com/reds-heig/logisim-evolution).

## Adding vector-test

Add JCenter to the list of repositories if not already included.

```groovy
repositories {
    jcenter()
}
```

### Gradle
```groovy
compile 'com.zelkatani:vector-test:0.1'
```

## Usage

vector-test has its own DSL/Builder implemented.

```kotlin
val table = buildTable {
    header {
        item("A")       // one width column.
        item("B", 15)   // 31 bit wide column.
        item("C", 16)   // 32 bit wide column.
    }

    row(0, 0, 0) // A row can be described in multiple ways.
    row {
        entry(0)
        entry(EntryType.HEXADECIMAL, "0x00000000000000F") // 15
        entry(EntryType.BINARY, "0000000000001111")
    }
}

// the table is modeled like this.
// NOTE: the below table is far prettier than the actual result when printing.
/*
A B[15]             C[16]
0 0                 0
0 0x00000000000000F 0000000000001111
*/

val file = table.exportToFile("crudely-tested-addition.txt")
```

There is also a function type used to mass create rows.

```kotlin
val addFunction = object : Function<Int> {
    override fun eval(fields: IntArray): Array<Int> {
        require(fields.size == 2) {
            "The add function requires exactly two parameters."
        }
    
        val a = field[0]
        val b = field[1]
        
        return arrayOf(a + b)
    }
}

val exportFileName = "function-generated.txt"

// A pair can be passed to this function to handle easy writing.
// The first value in the pair is the name of the file to export.
// The second value in the pair controls if file writing happens as we go, or at the end.
val table = buildTable(exportFileName to true) {
    header {
        item("A", 32)
        item("B", 32)
        item("C", 32)
    }

    val aRange = (0..1000 step 5).toList()
    val bRange = setOf(1, 3, 8, 2020).toList()
    functionPermutations(addFunction, aRange, bRange)
}

// table is now populated with the cartesian product 
// of aRange and bRange mapped into addFunction.
val file = File(exportFileName)
```
