# Truffula Notes
As part of Wave 0, please fill out notes for each of the below files. They are in the order I recommend you go through them. A few bullet points for each file is enough. You don't need to have a perfect understanding of everything, but you should work to gain an idea of how the project is structured and what you'll need to implement. Note that there are programming techniques used here that we have not covered in class! You will need to do some light research around things like enums and and `java.io.File`.

PLEASE MAKE FREQUENT COMMITS AS YOU FILL OUT THIS FILE.

## App.java
- Seems like this is where the app runs
- A lot of options can be passed in when the app is ran
- It seems like we are tasked with creating a TruffulaOptions object using the args. Wonder if Ill be reading from the command line

## ConsoleColor.java
- I've used Enums in visual scripting before but not Java.
- From what I understand, this enum just provides escape codes for colors on strings that we will apply to messages based on if the user wants a colored output or not
- Looks straightforward, it will be interesting how I will select the color from this enum though, might have to look into that


## ColorPrinter.java / ColorPrinterTest.java
- Seems like this is just a class that will be used elsewhere
- Looks pretty straight forward, we will call on this when printing colored text
- Testing seems minimal, need to add more tests.
- The way tests are structured is easy to understand, I think I can replicate with different tests

## TruffulaOptions.java / TruffulaOptionsTest.java
- Im guessing this is the object we construct in the App.java to actually let our program know the options we the user inputted from the console
- I kind of understand its functionality, just wondering how it's going to fit in with everything else in the program
- Will need to write more tests for different types of inputs to the console, just wondering how to write these tests as a lot of the techniques are new here

## TruffulaPrinter.java / TruffulaPrinterTest.java
- This seems to be where most of our logic will be in terms of getting the structure/message 
- Seems pretty straight forward, this looks like another objet we will call to given the root
- Interesting test cases here, to be honest it's a bit overwhelming, not sure if we are faking file structures or we are giving it our own. I need to do more digging for this.

## AlphabeticalFileSorter.java
- Looks like a utility to sort files by alphabetical order, im guessing the TruffulaPrinter will call this to sort the files before printing them