// -----------------------------------------------------------------
// Project: Part 1, Summary
// -----------------------------------------------------------------
import khoury.CapturedResult
import khoury.EnabledTest
import khoury.captureResults
import khoury.fileExists
import khoury.fileReadAsList
import khoury.input
import khoury.linesToString
import khoury.reactConsole
import khoury.runEnabledTests
import khoury.testSame
// You are going to design an application to allow a user to
// self-study using flash cards. In this part of the project,
// a user will...
//
// 1. Be prompted to choose from a menu of available flash
//    card decks; this menu will repeat until a valid
//    selection is made.
//
// 2. Proceed through each card in the selected deck,
//    one-by-one. For each card, the front is displayed,
//    and the user is allowed time to reflect; then the
//    back is displayed; and the user is asked if they
//    got the correct answer.
//
// 3. Once the deck is exhausted, the program outputs the
//    number of self-reported correct answers and ends.
//

// Of course, we'll design this program step-by-step, AND
// you've already done pieces of this in homework!!
// (Note: you are welcome to leverage your prior work and/or
// code found in the sample solutions & lecture notes.)
//

// Lastly, here are a few overall project requirements...
// - Since mutation hasn't been covered in class, your design is
//   NOT allowed to make use of mutable variables and/or lists.
// - As included in the instructions, all interactive parts of
//   this program MUST make effective use of the reactConsole
//   framework.
// - Staying consistent with our Style Guide...
//   * All functions must have:
//     a) a preceding comment specifying what it does
//     b) an associated @EnabledTest function with sufficient
//        tests using testSame
//   * All data must have:
//     a) a preceding comment specifying what it represents
//     b) associated representative examples
// - You will be evaluated on a number of criteria, including...
//   * Adherence to instructions and the Style Guide
//   * Correctly producing the functionality of the program
//   * Design decisions that include choice of tests, appropriate
//     application of list abstractions, and task/type-driven
//     decomposition of functions.
//

// -----------------------------------------------------------------
// Data design
// (Hint: see Homework 3, Problem 2)
// -----------------------------------------------------------------

// TODO 1/2: Design the data type FlashCard to represent a single
//           flash card. You should be able to represent the text
//           prompt on the front of the card as well as the text
//           answer on the back. Include at least 3 example cards
//           (which will come in handy later for tests!).
//

data class FlashCard(val front: String, val back: String)
val firstFlashcard = FlashCard("What is the capital of California?", "Sacramento")
val secondFlashcard = FlashCard("What's my name?", "Sandeep")
val thirdFlashcard = FlashCard("Last name?", "Salwan")

// TODO 2/2: Design the data type Deck to represent a deck of
//           flash cards. The deck should have a name, as well
//           as a Kotlin list of flash cards.
//
//           Include at least 2 example decks based upon the
//           card examples above.

data class Deck(val name: String, val list: List<FlashCard>)

val firstDeck = Deck("First", listOf(firstFlashcard, secondFlashcard))
val secondDeck = Deck("Second", listOf(firstFlashcard, secondFlashcard, thirdFlashcard))

// -----------------------------------------------------------------
// -----------------------------------------------------------------
// Generating flash cards
// -----------------------------------------------------------------

// One benefit of digital flash cards is that sometimes we can
// use code to produce cards that match a known pattern without
// having to write all the fronts/backs by hand!
//

// TODO 1/1: Design the function perfectSquares that takes a
//           count (assumed to be positive) and produces the
//           list of flash cards that tests that number of the
//           first squares.
//
//           For example, the first three perfect squares...
//
//            1. front (1^2 = ?), back (1)
//            2. front (2^2 = ?), back (4)
//            3. front (3^2 = ?), back (9)
//
//           have been supplied as named values.
//
//           Hint: you might consider combining your
//                 kthPerfectSquare function from Homework 1
//                 with the list constructor in Homework 3.
//
// Takes a count and returns list using kthPerfectSquare from HW1 logic
fun perfectSquares(input: Int): List<FlashCard> {
    fun createNewSquare(input: Int): FlashCard {
        return FlashCard("${input + 1}^2 = ?", "${(input + 1) * (input + 1)}")
    }
    return List<FlashCard>(input, ::createNewSquare)
}

// Initializing variables based on the function
// Front
val square1Front = "1^2 = ?"
val square2Front = "2^2 = ?"
val square3Front = "3^2 = ?"
// Back
val square1Back = "1"
val square2Back = "4"
val square3Back = "9"

// Creating 3 separate decks
val squaredAlpha = FlashCard(square1Front, square1Back)
val squaredBeta = FlashCard(square2Front, square2Back)
val squaredCharlie = FlashCard(square3Front, square3Back)

// Creating a list of decks that will be used for tests
val newListSquare = listOf(squaredAlpha, squaredBeta, squaredCharlie)

// Testing function
@EnabledTest
fun testPerfectSquares() {
    testSame(perfectSquares(0), listOf(), "none")
    testSame(perfectSquares(1), newListSquare.take(1), "first")
    testSame(perfectSquares(2), newListSquare.take(2), "second")
    testSame(perfectSquares(3), newListSquare.take(3), "third")
}

// Creating 3 Decks I will be using in the end
// I am able to build it now that I built PerfectSquares function
// -----------------------------------------------------------------
// Perfect Squares Deck
val perfectSquaresDeck = Deck("Perfect Square", perfectSquares(3))

// Fruit color flashcards
val fruitCard1 = FlashCard("What color is an apple?", "Red")
val fruitCard2 = FlashCard("What color is a banana?", "Yellow")
val fruitCard3 = FlashCard("What color is an orange?", "Orange")

// List of fruit flashcards
val fruitsDeck = Deck("Fruit Colors Deck", listOf(fruitCard1, fruitCard2, fruitCard3))

// Mame-related flashcards
val nameCard1 = FlashCard("What's my first name?", "Sandeep")
val nameCard2 = FlashCard("Last name?", "Salwan")
val nameCard3 = FlashCard("My nickname?", "Sandy")

// List of name flashcards
val myNameDeck = Deck("My Name Deck", listOf(nameCard1, nameCard2, nameCard3))

// Creating deckOptions for later use
val temp = fileReadAsList("example.txt")
val flashes = temp.map(::stringToCard)
val deckFile = Deck("File", flashes)
// 1. Construct a list of options
// (ala the instructions above)

val deckOptions = listOf(perfectSquaresDeck, fruitsDeck, deckFile)
val newDeckMade = deckOptions
// -----------------------------------------------------------------
// Files of cards
// -----------------------------------------------------------------

// Consider a simple format for storing flash cards in a file:
// each card is a line in the file, where the front comes first,
// separated by a "pipe" character ('|'), followed by the text
// on the back of the card.
//
// import java.io.FileWriter

val charSep = "|"

// TODO 1/3: Design the function cardToString that takes a flash
//           card as input and produces a string according to the
//           specification above ("front|back"). Make sure to
//           test all your card examples!
//
// Turns card to a string
fun cardToString(f: FlashCard): String {
    return "${f.front}${charSep}${f.back}"
}

// Testing cardToString
@EnabledTest
fun testCardToString() {
    testSame(
        cardToString(firstFlashcard),
        "What is the capital of California?|Sacramento",
        "first",
    )
    testSame(
        cardToString(secondFlashcard),
        "What's my name?|Sandeep",
        "second",
    )
}

// TODO 2/3: Design the function stringToCard that takes a string,
//           assumed to be in the format described above, and
//           produces the corresponding flash card.
//
//           Hints:
//           - look back to how we extracted data from CSV
//             (comma-separated value) files (such as in
//             Homework 3)!
//           - a great way to test: for each of your card
//             examples, pass them through the function in TODO
//             1 to convert them to a string; then, pass that
//             result to this function... you *should* get your
//             original flash card back :)'
//
//

// Takes a string and outputs to a flashcard
fun stringToCard(input: String): FlashCard {
// AD // A|D
    val parts = input.split("|")
    if (parts.size != 2) {
        throw IllegalArgumentException("Invalid input format. Expected 'front|back'.")
    }
    val front = parts[0]
    val back = parts[1]
    return FlashCard(front, back)
}

// Testing stringToCard function
@EnabledTest
fun testStringToCard() {
    testSame(
        stringToCard("What is the capital of California?|Sacramento"),
        FlashCard("What is the capital of California?", "Sacramento"),
        "first",
    )
    testSame(
        stringToCard("What is my dog's name?|Simba"),
        FlashCard("What is my dog's name?", "Simba"),
        "first",
    )
    // testSame(

    //     stringToCard("What is capital of California?|Sacram|ento"),
    //     FlashCard("What is capital of California?","Sacramento"),
    //     "second",
    // )
}

// TODO 3/3: Design the function readCardsFile that takes a path
//           to a file and produces the corresponding list of
//           flash cards found in the file.
//
//           If the file does not exist, return an empty list.
//           Otherwise, you can assume that every line is
//           formatted in the string format we just worked with.
//
//           Hint:
//           - Think about how HW3-P1 effectively used an
//             abstraction to process all the lines in a
//             file assuming a known pattern.
//           - We've provided an "example.txt" file that you can
//             use for testing if you'd like; also make sure to
//             test your function when the supplied file does not
//             exist!
//
// Goes through a gile and prints out list of flashcard
fun readCardsFile(num: String): List<FlashCard> {
    if (!(fileExists(num))) {
        return emptyList<FlashCard>()
    } else {
        val list = fileReadAsList(num)

        // for(i in list.size())
        // {
        //     stringToCard(list[i])
        // }
        return list.mapNotNull { l ->
            try {
                stringToCard(l)
            } catch (e: IllegalArgumentException) {
                println("Skipping invalid line")
                null
            }
        }
    }
}

// Testing readCardsFile
@EnabledTest
fun testreadCardsFile() {
    testSame(
        readCardsFile("example.txt"),
        listOf(
            FlashCard("What's the NEU President name?", "Aoun"),
            FlashCard("Whos coming to NEU?", "Boogie wit the hoodie"),
            FlashCard("Who is my best friend?", "Aryan"),
        ),
        "first",
    )
    testSame(
        readCardsFile("invalid.txt"),
        emptyList<FlashCard>(),
        "second",
    )
}

// -----------------------------------------------------------------
// Processing a self-report
// (Hint: see Homework 2)
// -----------------------------------------------------------------

// In our program, we will ask for a self-report as to whether
// the user got the correct answer for a card, SO...

// TODO 1/1: Finish designing the function isPositive that
//           determines if the supplied string starts with
//           the letter "y" (either upper or lowercase).

//           You've been supplied with a number of tests - make
//           sure you understand what they are doing!
//
// Sees if function starts with y
fun isPositive(input: String): Boolean {
    return(input.startsWith("y", ignoreCase = true))
}

// Testing isPositive function
@EnabledTest
fun testIsPositive() {
    fun helpTest(
        str: String,
        expected: Boolean,
    ) {
        testSame(isPositive(str), expected, str)
    }

    helpTest("yes", true)
    helpTest("Yes", true)
    helpTest("YES", true)
    helpTest("yup", true)

    helpTest("nope", false)
    helpTest("NO", false)
    helpTest("nah", false)
    helpTest("not a chance", false)
    // should pass,
    // despite doing the wrong thing
    helpTest("indeed", false)
}
// have more descriptive name
// -----------------------------------------------------------------
// Choosing a deck from a menu
// -----------------------------------------------------------------

// Now let's work on providing a menu of decks from which a user
// can choose what they want to study.

// TODO 1/2: Finish design the function choicesToText that takes
//           a list of strings (assumed to be non-empty) and
//           produces the textual representation of a menu of
//           those options.
//
val promptMenu = "Enter your choice"

//  Converting a list of choices into a formatted menu text
fun choicesToText(input: List<String>): String {
    val menuOptions = input.mapIndexed { index, option -> "${index + 1}. $option" }
    return menuOptions.joinToString(separator = "\n") + "\n\n$promptMenu"
}

//           For example, given...
//
//           ["a", "b", "c"]
//
//           The menu would be...
//
//           "1. a
//            2. b
//            3. c
//
//            Enter your choice"
//
//            As you have probably guessed, this will be a key
//            piece of our rendering function :)
//
//            Hints:
//            - Think back to Homework 3 when we used a list
//              constructor to generate list elements based
//              upon an index.
//            - If you can produce a list of strings, the
//              linesToString function in the Khoury library
//              will bring them together into a single string.
//            - Make sure to understand the supplied tests!
//
// Testing  choicesToText
@EnabledTest
fun testChoicesToText() {
    val optA = "apple"
    val optB = "banana"
    val optC = "carrot"
    testSame(
        choicesToText(listOf(optA)),
        linesToString(
            "1. $optA",
            "",
            promptMenu,
        ),
        "one",
    )

    testSame(
        choicesToText(listOf(optA, optB, optC)),
        linesToString(
            "1. $optA",
            "2. $optB",
            "3. $optC",
            "",
            promptMenu,
        ),
        "three",
    )
}

// TODO 2/2: Finish designing the program chooseOption that takes
//           a list of decks, produces a corresponding numbered
//           menu (1-# of decks, each showing its name), and
//           returns the deck corresponding to the number entered.
//           (Of course, keep displaying the menu until a valid
//           number is entered.)
//
//           Hints:
//            - Review the "Valid Number Example" of reactConsole
//              as one example of how to validate input. In this
//              case, however, since we know that we have a valid
//              range of integers, we can simplify the state
//              representation significantly :)
//            - To help you get started, the chooseOption function
//              has been written, but you must complete the helper
//              functions; look to the comments below for guidance.
//              You can then play "signature detective" to figure
//              out the parameters/return type of the functions you
//              need to write :)
//            - Lastly, as always, don't forget to sufficiently
//              test all the functions you write in this problem!
//
// Prints the deck name
fun getDeckName(deck: Deck): String {
    return deck.name
}

// Test for getDeckname
@EnabledTest
fun testGetDeckName() {
    // first sample decks
    val deck1 = Deck("Deck 1", emptyList())
    val deck2 = Deck("Deck 2", emptyList())
    val deck3 = Deck("Deck 3", emptyList())
    val deck4 = Deck("Deck 4", emptyList())

    // Test cases
    testSame(getDeckName(deck1), "Deck 1", "Deck 1")
    testSame(getDeckName(deck2), "Deck 2", "Deck 2")
    testSame(getDeckName(deck3), "Deck 3", "Deck 3")
    testSame(getDeckName(deck4), "Deck 4", "Deck 4")
}

// // keepIfValid  takes the typed input as a string and
// the valid indices of the decks. If the user did not type a valid integer,
// or not one in [1, size], return -1; othrwise, return the string converted to an
// integer, but subtract 1, which makes it a valid list index.
fun keepIfValid(
    input: String,
    indices: IntRange,
): Int {
    val choice = input.toIntOrNull()
    if (choice != null && choice in 1..indices.count()) {
        return choice - 1
    }
    return -1
}

// Testing Function keepIfValid
@EnabledTest
fun testkeepIfValid() {
    testSame(keepIfValid("3", 1..5), 2, "In Bounds ")
    testSame(keepIfValid("6", 1..5), -1, "Above bounds")
    testSame(keepIfValid("0", 1..5), -1, "Below bounds")
    testSame(keepIfValid("azdsaw", 1..5), -1, "Non-numeric")
}

// main chooseOption function
fun chooseOption(decks: List<Deck>): Deck {
    // since the event handlers will need some info about
    // the supplied decks, the functions inside
    // chooseOption provide info about them while the
    // parameter is in scope

// Design the function renderDeckOptions that takes an integer state
// and returns the textual representation of the menu for choosing a deck.
    fun renderDeckOptions(state: Int): String {
        return choicesToText(decks.map(::getDeckName))
    }

// Design the function transitionOptionChoice that takes the ignoredState (current state)
// and the typed keyboard input as a string and returns the next state.
    fun transitionOptionChoice(
        ignoredState: Int,
        kbInput: String,
    ): Int {
        return keepIfValid(kbInput, decks.indices)
    }

// Design the function validChoiceEntered that checks if a valid choice has been entered.
    fun validChoiceEntered(state: Int): Boolean {
        return state in decks.indices
    }

// Design the function choiceAnnouncement that takes the selected deck name and
// returns an announcement.
    fun choiceAnnouncement(deckIndex: Int): String {
        return "You chose: ${decks[deckIndex].name}"
    }

    while (true) {
        println(choicesToText(decks.map(::getDeckName)))
        val userInput = readLine() ?: continue
        val deckIndex = keepIfValid(userInput, decks.indices)
        if (deckIndex != -1) {
            println(choiceAnnouncement(deckIndex))
            return decks[deckIndex]
        } else {
            println("Invalid Input")
        }
    }
    // Return the selected deck using reactConsole.
    return decks[
        reactConsole(
            initialState = -1,
            stateToText = ::renderDeckOptions,
            nextState = ::transitionOptionChoice,
            isTerminalState = ::validChoiceEntered,
            terminalStateToText = ::choiceAnnouncement,
        ),
    ]
}

@EnabledTest
fun testChooseOption() {
    // helper function. use capture results
    //
    // println("You need to input 1, then 4, then 3, then abc, then 2")
    val decks =
        listOf(
            Deck("Deck1", listOf(FlashCard("Front1", "Back1"))),
            Deck("Deck2", listOf(FlashCard("Front2", "Back2"))),
            Deck("Deck3", listOf(FlashCard("Front3", "Back3"))),
        )

    testSame(
        captureResults(
            { chooseOption(decks) },
            "1",
        ),
        CapturedResult(
            decks[0],
            listOf("1. Deck1", "2. Deck2", "3. Deck3", "", "Enter your choice", "You chose: Deck1"),
        ),
    )
    testSame(
        captureResults(
            { chooseOption(decks) },
            "4",
            "3",
        ),
        CapturedResult(
            decks[2],
            listOf(
                "1. Deck1", "2. Deck2", "3. Deck3", "", "Enter your choice",
                "Invalid Input", "1. Deck1", "2. Deck2", "3. Deck3", "", "Enter your choice", "You chose: Deck3",
            ),
        ),
    )
}

// -----------------------------------------------------------------
// Studying a deck
// -----------------------------------------------------------------

// Now let's design a program to allow a user to study through a
// supplied deck of flash cards.

// TODO 1/2: Design the data type StudyState to keep track of...
//           - which card you are currently studying in the deck
//           - are you looking at the front or back
//           - how many correct answers have been self-reported
//             thus far
//
//           Create sufficient examples so that you convince
//           yourself that you can represent any situation that
//           might arise when studying a deck.
//
//           Hints:
//           - Look back to the reactConsole problems in HW2 and
//             HW3; the former involved keeping track of a count
//             of loops (similar to the count of correct answers),
//             and the latter involved options for keeping track
//             of where you are in a list with reactConsole.
//
data class StudyState(val cardIndex: Int, val isFront: Boolean, val correctAnswers: Int)

// Examples to convince myself that I can represent any situation that
// might arise when studying a deck.
val firstStudyStateExample = StudyState(cardIndex = 10, isFront = true, correctAnswers = 8)
val secondStudyStateExample = StudyState(cardIndex = 0, isFront = true, correctAnswers = 8)
val thirdStudyStateExample = StudyState(cardIndex = 5, isFront = false, correctAnswers = 8)
val fourthStudyStateExample = StudyState(cardIndex = -1, isFront = false, correctAnswers = 8)

// TODO 2/2: Now, using reactConsole, design the program studyDeck
//           that for each card in a supplied deck, allows the
//           user to...
//
//           1. see the front (pause and think)
//           2. see the back
//           3. respond as to whether they got the answer
//
//           At the end, the user is told how many they self-
//           reported as correct (and this number is returned).
//
//           You have been supplied some prompts for steps #1
//           and #2 - feel free to change them if you'd like :)
//
//           Suggestions...
//           - Review the reactConsole videos/examples
//           - Start with studyDeck:
//             * write some tests to convince yourself you know
//               what your program is supposed to do!
//             * figure out how you'll create the initial state
//             * give names to the handlers you'll need
//             * how will you return the number correct?
//             * now comment-out this function, so that you can
//               design/test the handlers without interference :)
//
//           - For each handler...
//             * Play signature detective: based upon how it's
//               being used with reactConsole, what data will it
//               be given and what does it produce?
//             * Write some tests to convince yourself you know
//               its job.
//             * Write the code and don't move on till your tests
//               pass.
//            - Suggested ordering...
//              1. Am I done studying yet?
//              2. Rendering
//                 - It's a bit simpler to have a separate
//                   function for the terminal state.
//                 - The linesToString function is your friend to
//                   combine the card with the prompts.
//                 - Think about good decomposition when making
//                   the decision about front vs back content.
//              3. Transition
//                 - Start with the two main situations
//                   you'll find yourself in...
//                   > front->back
//                   > back->front
//                 - Then let a helper figure out how to handle
//                   the details of self-report
//
//            You've got this :-)
//
val studyThink = "Think of the result? Press enter to continue"
val studyCheck = "Correct? (Y)es/(N)o"

fun nextState(
    state: StudyState,
    userInput: String,
): StudyState {
    fun addFunction(a: String): Int {
        if (isPositive(a)) {
            return 1
        } else {
            return 0
        }
    }
    return when (state.isFront) {
        true -> StudyState(state.cardIndex, false, state.correctAnswers)
        false -> StudyState(state.cardIndex + 1, true, state.correctAnswers + addFunction(userInput))
    }
}

@EnabledTest
fun testNextState() {
    // first state below is used to use in below testsame functions
    val initialState = StudyState(0, true, 0)

    //  tests are below using the above references
    val state1 = nextState(initialState, "user input")
    testSame(state1.cardIndex, 0, "First")
    testSame(state1.isFront, false, "Second")
    testSame(state1.correctAnswers, 0, "Third")
    val state2 = nextState(state1, "1")
    testSame(state2.cardIndex, 1, "Fourth")
    testSame(state2.isFront, true, "Fifth")
}

// This takes in Deck and returns an int
fun studyDeck(deckInput: Deck): Int {
    val initialState = StudyState(0, true, 0)

// gives study options
    fun renderStudyOptions(state: StudyState): String {
        val currentCard = deckInput.list[state.cardIndex]
        if (state.isFront) {
            return "Front: ${currentCard.front}" + "\n$studyThink"
        } else {
            return "Back: ${currentCard.back}" + "\n$studyCheck"
        }

        // return "$message\n1. Show ${if (state.isFront) "Back" else "Front"}\n2. Report Correct\n3. Skip"
    }

    // Checks if state is terminal
    fun isTerminalState(state: StudyState): Boolean {
        return state.cardIndex == deckInput.list.size
    }

// gives message when finished
    fun terminalStateToText(state: StudyState): String {
        return "You've completed the deck. You reported ${state.correctAnswers} correct answers."
    }
// uses react console using all above resources
    val finalState =
        reactConsole(
            initialState = initialState,
            stateToText = ::renderStudyOptions,
            nextState = ::nextState,
            isTerminalState = ::isTerminalState,
            terminalStateToText = ::terminalStateToText,
        )

    return finalState.correctAnswers
}

@EnabledTest
fun testStudyDeck() {
    // helpTest(second_deck)
    // fun helpTest(deck: Deck): () -> Int {
    //     fun studyMyDeck(): Int {
    //     return studyDeck(deck)
    // }

    // return ::studyMyDeck
    fun helpTest() {
        studyDeck(firstDeck)
    }

    testSame(
        captureResults(
            ::helpTest,
            "A",
            "n",
            "Sandeep",
            "Y",
        ),
        // 1
        CapturedResult(
            Unit,
            "Front: What is the capital of California?",
            studyThink,
            "Back: Sacramento",
            studyCheck,
            "Front: What's my name?",
            studyThink,
            "Back: Sandeep",
            studyCheck,
            "You've completed the deck. You reported 1 correct answers.",
            // "Front: Last name?", "Think of the result?", Press enter to continue, Back: Salwan, Correct? (Y)es/(N)o")
        ),
    )

// helper function
    fun helpTest2() {
        studyDeck(secondDeck)
    }

    testSame(
        captureResults(
            ::helpTest2,
            "A",
            "n",
            "Sandeep",
            "Y",
        ),
        // 1
        CapturedResult(
            Unit,
            "Front: What is the capital of California?",
            studyThink,
            "Back: Sacramento",
            studyCheck,
            "Front: What's my name?",
            studyThink,
            "Back: Sandeep",
            studyCheck,
            "Front: Last name?",
            studyThink,
            "Back: Salwan",
            studyCheck,
            "You've completed the deck. You reported 1 correct answers.",
        ),
    )
}

// -----------------------------------------------------------------
// Final app!
// -----------------------------------------------------------------

// Now you just get to put this all together 💃

// TODO 1/1: Design the function chooseAndStudy, where you'll
//           follow the comments in the supplied code to leverage
//           your prior work to allow the user to choose a deck,
//           study it, and return the number of correct self-
//           reports.
//
//           Your deck options MUST include at least one from each
//           of the following categories...
//
//           - Coded by hand (such as an example in data design)
//           - Read from a file (ala readCardsFile)
//           - Generated by code (ala perfectSquares)
//
//           Note: while this is an interactive program, you won't
//                 directly use reactConsole - instead, just call
//                 the programs you already designed above :)
//
//           And of course, don't forget to test at least two runs
//           of this completed program!
//
//           (And, consider adding this to main so you can see the
//           results of all your hard work so far this semester!)
// Compiling everything together
fun chooseAndStudy(): Int {
    fun deckFile(filePath: String): Deck {
        return Deck(filePath, readCardsFile(filePath))
    }

    // val newDeckMadeTest = listOf(
    // // TODO: at least...
    // // deck from file via readCardsFile,
    // deckFile("example.txt"),
    // // deck from code via perfectSquares
    // Deck("Perfect Squares",perfectSquares(3)),
    // // deck hand-coded
    // myNameDeck,
    // )

    // 2. Use chooseOption to let the user
    //    select a deck newDeckMadeTest
    val deckChosen = chooseOption(deckOptions)

    // 3. Let the user study, return the
    //    number correctly answered
    return studyDeck(deckChosen)
}
// fun testchooseAndStudy()
// {

// } testing chooseAndStudy
@EnabledTest
fun testchooseAndStudy() {
    // helpTest(second_deck)
    // fun helpTest(deck: Deck): () -> Int {
    //     fun studyMyDeck(): Int {
    //     return studyDeck(deck)
    // }
    // return ::studyMyDeck
    fun helpchooseAndStudyTest(): () -> Int {
        fun studyHelper(): Int {
            return chooseAndStudy()
        }
        return ::studyHelper
        // testchooseAndStudy(first_deck)
    }

    testSame(
        captureResults(
            helpchooseAndStudyTest(),
            "2",
            "",
            "n",
            "",
            "n",
            "",
            "y",
        ),
        CapturedResult(
            1,
            "1. Perfect Square",
            "2. Fruit Colors Deck",
            "3. File",
            "",
            "Enter your choice",
            "You chose: Fruit Colors Deck",
            "Front: What color is an apple?",
            "Think of the result? Press enter to continue",
            "Back: Red",
            "Correct? (Y)es/(N)o",
            "Front: What color is a banana?",
            "Think of the result? Press enter to continue",
            "Back: Yellow",
            "Correct? (Y)es/(N)o",
            "Front: What color is an orange?",
            "Think of the result? Press enter to continue",
            "Back: Orange",
            "Correct? (Y)es/(N)o",
            "You've completed the deck. You reported 1 correct answers.",
        ),
    )
    // second test
    testSame(
        captureResults(
            helpchooseAndStudyTest(),
            "2",
            "",
            "n",
            "",
            "y",
            "",
            "n",
        ),
        CapturedResult(
            1,
            "1. Perfect Square",
            "2. Fruit Colors Deck",
            "3. File",
            "",
            "Enter your choice",
            "You chose: Fruit Colors Deck",
            "Front: What color is an apple?",
            "Think of the result? Press enter to continue",
            "Back: Red",
            "Correct? (Y)es/(N)o",
            "Front: What color is a banana?",
            "Think of the result? Press enter to continue",
            "Back: Yellow",
            "Correct? (Y)es/(N)o",
            "Front: What color is an orange?",
            "Think of the result? Press enter to continue",
            "Back: Orange",
            "Correct? (Y)es/(N)o",
            "You've completed the deck. You reported 1 correct answers.",
        ),
    )
}

// calling function
fun main() {
    chooseAndStudy()
}

runEnabledTests(this)
main()
