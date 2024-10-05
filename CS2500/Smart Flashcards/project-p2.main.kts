// -----------------------------------------------------------------
// Project: Part 2, Summary
// -----------------------------------------------------------------

// Since working on part 1 of the project, you've learned many
// approaches that will allow us to improve both the design of
// data/functions, as well as add new functionality!
//
// == Data/Function Design ==
// - You'll enhance each flash card to support an arbitrary
//   number of "tags" (i.e., string labels).
// - You'll generalize the meaning of a deck, such as to be
//   agnostic as to the very meaning of cards (and thus
//   support a wider variety of decks).
// - You'll enhance the menu system to be re-usable, as
//   well as to support quitting (i.e., leave without forcing a
//   selection).
//
// == Application Features ==
// - You'll implement a second method for interpreting
//   self-reported correctness of a card, this time using
//   some machine learning (ML) to process natural language (NLP);
//   the user will be able to select which method to use (since
//   both methods have their tradeoffs!).
// - When a user doesn't get a card correct (via self-report),
//   that card is placed at the back of the deck; thus, a deck
//   is only completed when a user gets all cards correct.
// - You'll provide deck options that are a subset of cards
//   containing a particular tag (e.g., all "hard" cards, or
//   those in the topic of "science").
// - Once the program is run, the user will be able to study
//   as many decks as they wish, selecting subsequent decks
//   from the menu until they quit.

// Of course, we'll design this program step-by-step :)

// When designing this enhanced project, you are welcome to draw
// upon your project part 1, our sample solutions (for part 1, and
// any homework), and/or lecture notes as you see fit & helpful.

// Lastly, here are a few overall project requirements...
// - Now that mutation has been covered, you may use it (unless
//   otherwise stated in the instructions); however, your usage
//   will be evaluated based upon the guidelines from class.
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
//     c) for classes with member functions, an associated
//        @EnabledTest function with sufficient tests for all
//        the member functions of the class
// - You will be evaluated on a number of criteria, including...
//   * Adherence to instructions and the Style Guide
//   * Correctly producing the functionality of the program
//   * Design decisions that include choice of tests, appropriate
//     application of programming approaches (e.g., sequence
//     abstractions, recursion, mutation), and task/type-driven
//     decomposition of functions.
//

// -----------------------------------------------------------------
// Flash Card data design
// (Hint: see Homework 5, Problem 3)
// -----------------------------------------------------------------

// TODO 1/1: Design the data type TaggedFlashCard to represent a
//           single flash card.
//
//           You should be able to represent the text prompt on
//           the front of the card, the text answer on the back,
//           as well as any number of textual tags (such as "hard"
//           or "science" -- this shouldn't come from any fixed
//           set of options, but truly open to however someone
//           wishes to categorize their cards).
//
//           Each card should have two member functions:
//           - isTagged, which determines if the card has a
//             supplied tag (e.g., has this card been tagged
//             as "hard"?)
//           - fileFormat, which produces a textual representation
//             of the card as "front|back|tag1,tag2,..."; that is
//             all three parts of the card separated with the pipe
//             ('|') character, and further separate any tags with
//             a comma
//
//           Include *at least* 3 example cards (which will come
//           in handy later for tests!), and make sure to test
//           the required member functions.
//

import khoury.CapturedResult
import khoury.EnabledTest
import khoury.captureResults
import khoury.fileReadAsList
import khoury.input
import khoury.isAnInteger
import khoury.linesToString
import khoury.reactConsole
import khoury.runEnabledTests
import khoury.testSame

// (just useful values for
// the separation characters)
val sepCard = "|"
val sepTag = ","

// repersents a flash card with front and back text and tag descriptors for the entire thing
data class TaggedFlashCard(val frontText: String, val backText: String, val tags: List<String>) {
    // checks if a given tag is shared by the flashcard
    fun isTagged(givenTag: String): Boolean {
        for (tag in tags) {
            if (givenTag == tag) {
                return true
            }
        }
        return false
    }

    // puts the flashcard into a text format
    fun fileFormat(): String {
        var initialString = "$frontText$sepCard$backText$sepCard"
        for (tag in tags) {
            initialString = "$initialString$tag$sepTag"
        }
        return initialString.dropLast(1)
    }
}

val problem1: TaggedFlashCard =
    TaggedFlashCard(
        "What is the capital of the U.S.A",
        "Washington D.C.",
        listOf("Easy", "Geography"),
    )
val problem2: TaggedFlashCard =
    TaggedFlashCard(
        "x + 5 = 20 - 5",
        "10",
        listOf("Intermediate", "Math"),
    )
val problem3: TaggedFlashCard =
    TaggedFlashCard(
        "What is the meaning of life",
        "42",
        listOf("Hard", "Philosophy"),
    )

@EnabledTest
fun testTaggedFlashCard() {
    // test fileFormat
    testSame(
        problem1.fileFormat(),
        "What is the capital of the U.S.A|Washington D.C.|Easy,Geography",
        "check 1st prob format",
    )
    testSame(
        problem2.fileFormat(),
        "x + 5 = 20 - 5|10|Intermediate,Math",
        "check 2nd prob format",
    )
    testSame(
        problem3.fileFormat(),
        "What is the meaning of life|42|Hard,Philosophy",
        "check 3rd prob format",
    )
    // test isTagged
    testSame(
        problem1.isTagged("Geography"),
        true,
        "check 1st prob tags success",
    )
    testSame(
        problem2.isTagged("Geography"),
        false,
        "check 1st prob tags failure",
    )
    testSame(
        problem3.isTagged("Hard"),
        true,
        "check 3rd prob tags success",
    )
    testSame(
        problem2.isTagged(""),
        false,
        "check 2nd prob tag empty",
    )
}

// -----------------------------------------------------------------
// Files of tagged flash cards
// -----------------------------------------------------------------

// Now that we have our updated cards, let's update how we read
// them from files.

// TODO 1/2: Design the function stringToTaggedFlashCard that
//           takes a string, assumed to be in the format described
//           for the fileFormat member function above, and produces
//           the corresponding tagged flash card.
//
//           Hint: review part 1 of the project, TODO 2/3
//

// TODO 2/2: Design the function readTaggedFlashCardsFile that
//           takes a path to a file and produces a list of
//           tagged flash cards.
//
//           If the file does not exist, return an empty list.
//           Otherwise, you can assume that every line is
//           formatted in the string format we just worked with.
//
//           Hint:
//           - Review part 1 of the project, TODO 3/3
//           - We've provided an "example_tagged.txt" file that you
//             can use for testing if you'd like; also make sure to
//             test your function when the supplied file does not
//             exist!
//

// gives path to file
val exampleAddress: String = "example_tagged.txt"
val badAddress: String = "useless_path.txt"

// turns a string into a tagged flash card
// string needs to be format of frontText|backText|tag1,tag2,tag3
fun stringToTaggedFlashCard(givenString: String): TaggedFlashCard {
    val seperateFormat: List<String> = givenString.split(sepCard)
    val seperateTags: List<String> = seperateFormat[2].split(sepTag)
    return TaggedFlashCard(seperateFormat[0], seperateFormat[1], seperateTags)
}

@EnabledTest
fun testStringToTaggedFlashCard() {
    testSame(
        stringToTaggedFlashCard(
            "What is the capital of the U.S.A|Washington D.C.|Easy,Geography",
        ),
        problem1,
        "test problem1",
    )
    testSame(
        stringToTaggedFlashCard(
            "x + 5 = 20 - 5|10|Intermediate,Math",
        ),
        problem2,
        "test problem2",
    )
    testSame(
        stringToTaggedFlashCard(
            "What is the meaning of life|42|Hard,Philosophy",
        ),
        problem3,
        "test problem3",
    )
}

// goes to specified file and derives flashcards from it
// shows up as a empty list if file does not exist
fun readTaggedFlashCardsFile(givenFile: String): List<TaggedFlashCard> {
    val fileString: List<String> = fileReadAsList(givenFile)
    return fileString.map(::stringToTaggedFlashCard)
}

@EnabledTest
fun testReadFlashCardsFile() {
    testSame(
        readTaggedFlashCardsFile(exampleAddress),
        listOf(
            TaggedFlashCard("c", "3", listOf("hard", "science")),
            TaggedFlashCard("d", "4", listOf("hard")),
        ),
        "right address test",
    )
    testSame(
        readTaggedFlashCardsFile(badAddress),
        emptyList<TaggedFlashCard>(),
        "invalid address test",
    )
}

// -----------------------------------------------------------------
// Deck design
// -----------------------------------------------------------------

// If you think about it, once a deck has been selected, our study
// application doesn't need much information about cards to work...
// in fact, it doesn't even need the concept of a card. Consider
// the following:
//

// The deck is either exhausted,
// showing the question, or
// showing the answer
enum class DeckState {
    EXHAUSTED,
    QUESTION,
    ANSWER,
}

// Basic functionality of any deck
interface IDeck {
    // The state of the deck
    fun getState(): DeckState

    // The currently visible text
    // (or null if exhausted)
    fun getText(): String?

    // The number of question/answer pairs
    // (does not change when question are
    // cycled to the end of the deck)
    fun getSize(): Int

    // Shifts from question -> answer
    // (if not QUESTION state, returns the same IDeck)
    fun flip(): IDeck

    // Shifts from answer -> next question (or exhaustion);
    // if the current question was correct it is discarded,
    // otherwise cycled to the end of the deck
    // (if not ANSWER state, returns the same IDeck)
    fun next(correct: Boolean): IDeck
}

// This contract of operations will allow our study application to
// work with a variety of sources, including lists and even code
// that never explicitly stores cards!
//
// (For a similar problem, see Homework 6, Problem 3, TODO 2,
// where you implemented stateful classes to integrate with an
// object-oriented reactConsole.)
//

// TODO 1/2: Design TFCListDeck to implement the IDeck interface
//           for a supplied list of tagged flash cards. For this
//           problem your class must have *no* mutable state and
//           all member data should be private.
//
//           When testing, make sure to test the behavior of all
//           the member functions of the interface in a variety
//           of situations.
//
//           Hint: using default arguments can make your class
//                 easier to create initially, see...
//
//           kotlinlang.org/docs/functions.html#default-arguments
//

// Represents the state of a study session with the
// current cards being looked at and whether
// a flash card showcases the back, front, or if all cards are exhausted
data class TFCListDeck(
    private val givenCards: List<TaggedFlashCard>,
    private val givenState: DeckState,
) : IDeck {
    // The state of the deck
    override fun getState(): DeckState = givenState

    // The currently visible text
    // (or null if exhausted)
    override fun getText(): String? {
        return when (givenState) {
            DeckState.EXHAUSTED -> null
            DeckState.QUESTION -> givenCards[0].frontText
            DeckState.ANSWER -> givenCards[0].backText
        }
    }

    // The number of question/answer pairs
    // (does not change when question are
    // cycled to the end of the deck)
    override fun getSize(): Int = givenCards.count()

    // Shifts from question -> answer
    // (if not QUESTION state, returns the same IDeck)
    override fun flip(): IDeck {
        return when (givenState) {
            DeckState.QUESTION -> TFCListDeck(givenCards, DeckState.ANSWER)
            else -> TFCListDeck(givenCards, givenState)
        }
    }

    // Shifts from answer -> next question (or exhaustion);
    // if the current question was correct it is discarded,
    // otherwise cycled to the end of the deck
    // (if not ANSWER state, returns the same IDeck)
    override fun next(correct: Boolean): IDeck {
        return when (givenState) {
            DeckState.ANSWER ->
                if (correct) {
                    val newList: List<TaggedFlashCard> = givenCards.drop(1)
                    if (newList.isEmpty()) {
                        TFCListDeck(newList, DeckState.EXHAUSTED)
                    } else {
                        TFCListDeck(newList, DeckState.QUESTION)
                    }
                } else {
                    TFCListDeck(
                        givenCards.drop(1) + listOf(givenCards[0]),
                        DeckState.QUESTION,
                    )
                }
            else -> TFCListDeck(givenCards, givenState)
        }
    }
}

// example instances
val fullDeck: TFCListDeck =
    TFCListDeck(
        listOf(
            problem1,
            problem2,
            problem3,
        ),
        DeckState.QUESTION,
    )
val halfDeck: TFCListDeck =
    TFCListDeck(
        listOf(
            problem2,
        ),
        DeckState.ANSWER,
    )
val emptyDeck: TFCListDeck =
    TFCListDeck(
        emptyList<TaggedFlashCard>(),
        DeckState.EXHAUSTED,
    )

@EnabledTest
fun testTFCListDeck() {
    // test getState
    testSame(
        fullDeck.getState(),
        DeckState.QUESTION,
        "getState: question state",
    )
    testSame(
        halfDeck.getState(),
        DeckState.ANSWER,
        "getState: answer state",
    )
    testSame(
        emptyDeck.getState(),
        DeckState.EXHAUSTED,
        "getState: exhausted state",
    )
    // test getText
    testSame(
        fullDeck.getText(),
        "What is the capital of the U.S.A",
        "getText: question state",
    )
    testSame(
        halfDeck.getText(),
        "10",
        "getText: anwser state",
    )
    testSame(
        emptyDeck.getText(),
        null,
        "getText: exhausted state",
    )
    // test getSize
    testSame(
        fullDeck.getSize(),
        3,
        "getSize: full deck",
    )
    testSame(
        halfDeck.getSize(),
        1,
        "getSize: half deck",
    )
    testSame(
        emptyDeck.getSize(),
        0,
        "getSize: empty deck",
    )
    // test flip
    testSame(
        fullDeck.flip(),
        TFCListDeck(
            listOf(problem1, problem2, problem3),
            DeckState.ANSWER,
        ),
        "flip: full deck",
    )
    testSame(
        halfDeck.flip(),
        TFCListDeck(
            listOf(problem2),
            DeckState.ANSWER,
        ),
        "flip: half deck",
    )
    testSame(
        emptyDeck.flip(),
        TFCListDeck(
            emptyList<TaggedFlashCard>(),
            DeckState.EXHAUSTED,
        ),
        "flip: empty deck",
    )
    // test next
    testSame(
        fullDeck.flip().next(false),
        TFCListDeck(
            listOf(problem2, problem3, problem1),
            DeckState.QUESTION,
        ),
        "next: cycle deck",
    )
    testSame(
        fullDeck.next(true),
        TFCListDeck(
            listOf(problem1, problem2, problem3),
            DeckState.QUESTION,
        ),
        "next: full deck",
    )
    testSame(
        halfDeck.next(true),
        TFCListDeck(
            emptyList<TaggedFlashCard>(),
            DeckState.EXHAUSTED,
        ),
        "next: half deck correct",
    )
    testSame(
        halfDeck.next(false),
        TFCListDeck(
            listOf(problem2),
            DeckState.QUESTION,
        ),
        "next: half deck false",
    )
    testSame(
        emptyDeck.next(false),
        TFCListDeck(
            emptyList<TaggedFlashCard>(),
            DeckState.EXHAUSTED,
        ),
        "next: empty deck",
    )
}

// TODO 2/2: Now design PerfectSquaresDeck to implement the IDeck
//           interface. You are *not* allowed to generate any
//           flash cards, nor have mutable state; the goal is to
//           act as though it had a list produced by the
//           perfectSquares function in part 1 of the project,
//           but without ever having to generate all those cards!
//           Again, as is generally good practice, keep all your
//           member data private!
//
//           Hint: you will still need to keep track of the
//                 *sequence* of upcoming numbers (particularly
//                 as some may get cycled back due to incorrect
//                 responses).
//

// Repersents a study deck consisting of perfect squares
// Asks the square of certain numbers then shows them
data class PerfectSquaresDeck(
    private val upcoming: List<Int>,
    private val givenState: DeckState,
) : IDeck {
    // The state of the deck
    override fun getState(): DeckState = givenState

    // The currently visible text
    // (or null if exhausted)
    override fun getText(): String? {
        return when (givenState) {
            DeckState.EXHAUSTED -> null
            DeckState.QUESTION -> "${upcoming[0]} * ${upcoming[0]} = ?"
            DeckState.ANSWER -> "${(upcoming[0] * upcoming[0])}"
        }
    }

    // The number of question/answer pairs
    // (does not change when question are
    // cycled to the end of the deck)
    override fun getSize(): Int = upcoming.size

    // Shifts from question -> answer
    // (if not QUESTION state, returns the same IDeck)
    override fun flip(): IDeck {
        return when (givenState) {
            DeckState.QUESTION ->
                PerfectSquaresDeck(
                    upcoming,
                    DeckState.ANSWER,
                )
            else ->
                PerfectSquaresDeck(
                    upcoming,
                    givenState,
                )
        }
    }

    // Shifts from answer -> next question (or exhaustion);
    // if the current question was correct it is discarded,
    // otherwise cycled to the end of the deck
    // (if not ANSWER state, returns the same IDeck)
    override fun next(correct: Boolean): IDeck {
        return when (givenState) {
            DeckState.ANSWER ->
                if (correct) {
                    val newList: List<Int> = upcoming.drop(1)
                    if (newList.isEmpty()) {
                        PerfectSquaresDeck(newList, DeckState.EXHAUSTED)
                    } else {
                        PerfectSquaresDeck(newList, DeckState.QUESTION)
                    }
                } else {
                    PerfectSquaresDeck(
                        upcoming.drop(1) + listOf(upcoming[0]),
                        DeckState.QUESTION,
                    )
                }
            else -> PerfectSquaresDeck(upcoming, givenState)
        }
    }
}

val longSquaresDeck: PerfectSquaresDeck =
    PerfectSquaresDeck(
        listOf(1, 2, 3, 4, 5),
        DeckState.QUESTION,
    )
val shortSquaresDeck: PerfectSquaresDeck =
    PerfectSquaresDeck(
        listOf(2),
        DeckState.ANSWER,
    )
val emptySquaresDeck: PerfectSquaresDeck =
    PerfectSquaresDeck(
        listOf(),
        DeckState.EXHAUSTED,
    )

// example instances
@EnabledTest
fun testPerfectSquaresDeck() {
    // test getState
    testSame(
        longSquaresDeck.getState(),
        DeckState.QUESTION,
        "getState: question state",
    )
    testSame(
        shortSquaresDeck.getState(),
        DeckState.ANSWER,
        "getState: answer state",
    )
    testSame(
        emptySquaresDeck.getState(),
        DeckState.EXHAUSTED,
        "getState: exhausted state",
    )
    // test getText
    testSame(
        longSquaresDeck.getText(),
        "1 * 1 = ?",
        "getText: question state",
    )
    testSame(
        shortSquaresDeck.getText(),
        "4",
        "getText: anwser state",
    )
    testSame(
        emptySquaresDeck.getText(),
        null,
        "getText: exhausted state",
    )
    // test getSize
    testSame(
        longSquaresDeck.getSize(),
        5,
        "getSize: long deck",
    )
    testSame(
        shortSquaresDeck.getSize(),
        1,
        "getSize: short deck",
    )
    testSame(
        emptySquaresDeck.getSize(),
        0,
        "getSize: empty deck",
    )
    // test flip
    testSame(
        longSquaresDeck.flip(),
        PerfectSquaresDeck(
            listOf(1, 2, 3, 4, 5),
            DeckState.ANSWER,
        ),
        "flip: long deck",
    )
    testSame(
        shortSquaresDeck.flip(),
        PerfectSquaresDeck(
            listOf(2),
            DeckState.ANSWER,
        ),
        "flip: short deck",
    )
    testSame(
        emptySquaresDeck.flip(),
        PerfectSquaresDeck(
            emptyList<Int>(),
            DeckState.EXHAUSTED,
        ),
        "flip: empty deck",
    )
    // test next
    testSame(
        longSquaresDeck.flip().next(false),
        PerfectSquaresDeck(
            listOf(2, 3, 4, 5, 1),
            DeckState.QUESTION,
        ),
        "next: cycle deck",
    )
    testSame(
        longSquaresDeck.next(true),
        PerfectSquaresDeck(
            listOf(1, 2, 3, 4, 5),
            DeckState.QUESTION,
        ),
        "next: full deck",
    )
    testSame(
        shortSquaresDeck.next(true),
        PerfectSquaresDeck(
            emptyList<Int>(),
            DeckState.EXHAUSTED,
        ),
        "next: short deck correct",
    )
    testSame(
        shortSquaresDeck.next(false),
        PerfectSquaresDeck(
            listOf(2),
            DeckState.QUESTION,
        ),
        "next: short deck false",
    )
    testSame(
        emptySquaresDeck.next(false),
        PerfectSquaresDeck(
            emptyList<Int>(),
            DeckState.EXHAUSTED,
        ),
        "next: empty deck",
    )
}

// -----------------------------------------------------------------
// Menu design
// -----------------------------------------------------------------

// The chooseOption function in part 1 of the project was good, but
// let's see what we can do to improve upon it in two core ways...
//
// a) Part 1 allowed you to select from amongst decks, which means
//    you'd have to copy-paste if you wanted to have a menu of
//    other data (such as files, or months of the year); let's
//    make the function agnostic as to the type of the list items
//    being selected.
// b) Part 1 didn't allow for the possibility of not selecting an
//    option; let's add a quit feature!
//
// To help with (a), consider the following interface, which
// requires that a menu option be able to return a textual
// representation (that is then displayed in the menu!)...
//

// the only required capability for a menu option
// is to be able to render a title
interface IMenuOption {
    fun menuTitle(): String
}

// as well as the following general implementation (great for
// tests & examples), which satisfies the contract via pairing
// a value (of any type) with a name...

// a menu option with a single value and name
data class NamedMenuOption<T>(val option: T, val name: String) : IMenuOption {
    override fun menuTitle(): String = name
}

// individual examples, as well as a list
// (an example for a list of menu options!)
val opt1A = NamedMenuOption(1, "apple")
val opt2B = NamedMenuOption(2, "banana")
val optsExample = listOf(opt1A, opt2B)

// TODO 1/1: Finish designing the program chooseMenuOption that
//           takes a list (assumed to be non-empty) of any type
//           (as long as it implements the IMenuOption interface),
//           produces a corresponding numbered menu (1-# of list
//           items, each showing its menuTitle), and returns the
//           list item corresponding to the number entered (or null
//           if 0 was entered to indicate a desire to quit without
//           choosing an option). Keep displaying the menu until a
//           valid menu selection (or quitting) is indicated.
//
//           Hints:
//           - You'll find the code from chooseOption (in part 1)
//             to be a *very* good starting point.
//           - Homework 5, Problem 4, has a very similar interface,
//             which can give you an idea for how you'd use it.
//           - To help you get started, you have some examples
//             above and prompts below; a "stub" for the
//             chooseMenuOption function (to help with the
//             signature and overall structure); and a set of
//             tests that should pass once the program has been
//             completed.
//

// Some useful outputs
val menuPrompt = "Enter your choice (or 0 to quit)"
val menuQuit = "You quit"
val menuChoicePrefix = "You chose: "

// Returns the name of a deck
fun <T : IMenuOption> getMenuTitle(givenOption: T): String {
    return givenOption.menuTitle()
}

@EnabledTest
fun testGetMenuTitle() {
    testSame(
        getMenuTitle(opt1A),
        "apple",
        "test 1st",
    )
    testSame(
        getMenuTitle(opt2B),
        "banana",
        "test 2nd",
    )
    testSame(
        getMenuTitle(NamedMenuOption("", "")),
        "",
        "test empty deck name",
    )
}

// Prompts the user to select one option from a given list of texts
fun <T : IMenuOption> menuChoicesToText(givenList: List<T>): String {
    fun addNumber(index: Int): String {
        return "${index + 1}. ${givenList[index].menuTitle()}"
    }
    val decksText: List<String> = List<String>(givenList.count(), ::addNumber)
    return linesToString(decksText + listOf("", menuPrompt))
}

@EnabledTest
fun testChoicesToText() {
    testSame(
        menuChoicesToText(listOf()),
        linesToString(
            "",
            menuPrompt,
        ),
        "empty",
    )
    testSame(
        menuChoicesToText(listOf(opt1A)),
        linesToString(
            "1. ${opt1A.menuTitle()}",
            "",
            menuPrompt,
        ),
        "one",
    )
    testSame(
        menuChoicesToText(listOf(opt1A, opt2B)),
        linesToString(
            "1. ${opt1A.menuTitle()}",
            "2. ${opt2B.menuTitle()}",
            "",
            menuPrompt,
        ),
        "two",
    )
}

// Returns -2 if input subtracted by one is not in range
// return the subtracted down by one input otherwise
// If zero return -1
fun keepIfValid(
    input: String,
    range: IntRange,
): Int {
    if (isAnInteger(input)) {
        val returnInt = input.toInt() - 1
        if (returnInt in range) {
            return returnInt
        } else if (returnInt == -1) {
            return -1
        }
    }
    return -2
}

@EnabledTest
fun testKeepIfValid() {
    testSame(
        keepIfValid("wdfpoisjdiof", 1..5),
        -2,
        "test fail non int",
    )
    testSame(
        keepIfValid("", 1..5),
        -2,
        "test fail empty input",
    )
    testSame(
        keepIfValid("2", 0..0),
        -2,
        "test fail out of range",
    )
    testSame(
        keepIfValid("3", 3..8),
        -2,
        "test fail shift down",
    )
    testSame(
        keepIfValid("2", 1..5),
        1,
        "test correct",
    )
    testSame(
        keepIfValid("6", 1..5),
        5,
        "test correct shift down",
    )
    testSame(
        keepIfValid("0", 1..5),
        -1,
        "test quit",
    )
}

// Provides affirmation that a certain input has been done
fun choiceAnnouncement(input: String): String {
    return "$menuChoicePrefix$input"
}

@EnabledTest
fun testChoiceAnnouncement() {
    testSame(
        choiceAnnouncement("wow"),
        "You chose: wow",
        "test wow",
    )
    testSame(
        choiceAnnouncement(""),
        "You chose: ",
        "test empty",
    )
    testSame(
        choiceAnnouncement("2"),
        "You chose: 2",
        "test number",
    )
    testSame(
        choiceAnnouncement("ALLL"),
        "You chose: ALLL",
        "test all",
    )
}

// Provides an interactive opportunity for the user to choose
// an option or quit.
fun <T : IMenuOption> chooseMenuOption(options: List<T>): T? {
    // returns name
    fun renderMenuOptions(state: Int): String {
        return menuChoicesToText(options)
    }

    // checks if input is zero to quit or within the displayed options
    fun transitionOptionChoice(
        ignoredState: Int,
        kbInput: String,
    ): Int {
        return keepIfValid(kbInput, options.indices)
    }

    // Processes result of final reactConsole
    fun valueOrQuit(state: Int): T? {
        return when {
            (state in options.indices) -> options[state]
            else -> null
        }
    }

    // Checks if to proceed to terminal state
    fun checkValidity(state: Int): Boolean {
        return (state in options.indices || state == -1)
    }

    // Announces the final choice
    fun renderChoice(state: Int): String {
        return when {
            (state in options.indices) -> choiceAnnouncement(getMenuTitle(options[state]))
            else -> menuQuit
        }
    }

    return valueOrQuit(
        reactConsole(
            initialState = -2,
            stateToText = ::renderMenuOptions,
            nextState = ::transitionOptionChoice,
            isTerminalState = ::checkValidity,
            terminalStateToText = ::renderChoice,
        ),
    )
}

@EnabledTest
fun testChooseMenuOption() {
    testSame(
        captureResults(
            { chooseMenuOption(listOf(opt1A)) },
            "howdy",
            "0",
        ),
        CapturedResult(
            null,
            "1. ${opt1A.name}",
            "",
            menuPrompt,
            "1. ${opt1A.name}",
            "",
            menuPrompt,
            menuQuit,
        ),
        "quit",
    )

    testSame(
        captureResults(
            { chooseMenuOption(optsExample) },
            "hello",
            "10",
            "-3",
            "1",
        ),
        CapturedResult(
            opt1A,
            "1. ${opt1A.name}", "2. ${opt2B.name}", "", menuPrompt,
            "1. ${opt1A.name}", "2. ${opt2B.name}", "", menuPrompt,
            "1. ${opt1A.name}", "2. ${opt2B.name}", "", menuPrompt,
            "1. ${opt1A.name}", "2. ${opt2B.name}", "", menuPrompt,
            "${menuChoicePrefix}${opt1A.name}",
        ),
        "1",
    )

    testSame(
        captureResults(
            { chooseMenuOption(optsExample) },
            "3",
            "-1",
            "2",
        ),
        CapturedResult(
            opt2B,
            "1. ${opt1A.name}", "2. ${opt2B.name}", "", menuPrompt,
            "1. ${opt1A.name}", "2. ${opt2B.name}", "", menuPrompt,
            "1. ${opt1A.name}", "2. ${opt2B.name}", "", menuPrompt,
            "${menuChoicePrefix}${opt2B.name}",
        ),
        "2",
    )
}

// -----------------------------------------------------------------
// Machine learning for sentiment analysis
// -----------------------------------------------------------------

// In part 1 of the project, you designed isPositive as a way to
// interpret whether a student's self-report was positive or
// negative; in the world of Machine Learning (a subfield of
// Artificial Intelligence, or AI), this is an approach to
// "sentiment analysis" - a problem in Natural Language Processing
// (NLP) that seeks to analyze text to understand the emotional
// tone of some text.
//
// In this context, what you built was a "binary classifier" of
// text, meaning it output one of two values according to the input
// string. In Kotlin we can describe this input-output relationship
// using the following shortcut...

typealias PositivityClassifier = (String) -> Boolean

// This code simply means we can now use PositivityClassifier
// anywhere we would have used the type on the right (e.g.,
// as the type in a function's parameter or return type).
//
// Our goal is now to try and use a more sophisticated approach
// to sentiment analysis - one that learns positivity/negativity
// based upon a dataset of supplied examples. To represent such a
// dataset, consider the following type...

data class LabeledExample<E, L>(val example: E, val label: L)

// This associates a "label" (such as positive vs negative, or
// cat video vs boring) with an example. Here is one such dataset:

val datasetYN: List<LabeledExample<String, Boolean>> =
    listOf(
        LabeledExample("yes", true),
        LabeledExample("y", true),
        LabeledExample("indeed", true),
        LabeledExample("aye", true),
        LabeledExample("oh yes", true),
        LabeledExample("affirmative", true),
        LabeledExample("roger", true),
        LabeledExample("uh huh", true),
        LabeledExample("true", true),
        // just a visual separation of
        // the positive/negative examples
        LabeledExample("no", false),
        LabeledExample("n", false),
        LabeledExample("nope", false),
        LabeledExample("negative", false),
        LabeledExample("nay", false),
        LabeledExample("negatory", false),
        LabeledExample("uh uh", false),
        LabeledExample("absolutely not", false),
        LabeledExample("false", false),
    )

// FYI: we call this dataset "balanced" since it has an equal
//      number of examples of the labels (i.e., # true and #false).
//      Such a balance is *one* tool (of many) when trying to avoid
//      algorithmic bias (en.wikipedia.org/wiki/Algorithmic_bias).

// Notice that our simple heuristic of the first letter is pretty
// good according to this dataset, but will make some lucky
// guesses (e.g., "false") and some actual mistakes (e.g., "true").
// We have provided below that code, as well as a set of tests that
// reference our labeled dataset - make sure you understand all of
// this code (including the comments in the tests about when & how
// the heuristic is predictably getting the answer wrong).

// Heuristically determines if the supplied string
// is positive based upon the first letter being Y
fun isPositiveSimple(s: String): Boolean {
    return s.uppercase().startsWith("Y")
}

// tests that an element of the dataset matches
// with expectation of its correctness on a
// particular classifier
fun helpTestElement(
    index: Int,
    expectedIsCorrect: Boolean,
    isPos: PositivityClassifier,
) {
    testSame(
        isPos(datasetYN[index].example),
        when (expectedIsCorrect) {
            true -> datasetYN[index].label
            false -> !datasetYN[index].label
        },
        when (expectedIsCorrect) {
            true -> datasetYN[index].example
            false -> "${ datasetYN[index].example } <- WRONG"
        },
    )
}

@EnabledTest
fun testIsPositiveSimple() {
    val classifier = ::isPositiveSimple

    // correctly responds with positive
    for (i in 0..1) {
        helpTestElement(i, true, classifier)
    }

    // incorrectly responds with negative
    for (i in 2..8) {
        helpTestElement(i, false, classifier)
    }

    // correctly responds with negative, sometimes
    // due to luck (i.e., anything not starting
    // with the letter Y is assumed negative)
    for (i in 9..17) {
        helpTestElement(i, true, classifier)
    }
}

// One approach we *could* take is just to have the computer learn
// by rote memorization: that is, respond with the labeled answer
// from the dataset. But what about if the student supplies an
// input not in this list? The approach we'll try as a way to
// handle this situation is the following...
// - If the response is known in the dataset (independent of
//   upper/lower-case), use the associated label
// - Otherwise...
//   Find the 3 "closest" examples and respond with a majority
//   vote of their associated labels
//
// This algorithm will represent our attempt to "generalize"
// from the dataset; we know we'll always get certain responses
// correct, and we'll let our dataset inform the response of
// unknown inputs. As with all approaches based upon machine
// learning, this approach is likely to make mistakes (even those
// that we'll find confusing/comical), and so we should be
// judicious in how we apply the system in the world.
//
// Now let's build up this classifier, step-by-step :)
//

// TODO 1/5: When finding closest examples, and majority vote, it
//           will be helpful to be able to get the "top-k" of a
//           list by some measure; meaning, a function that can
//           get the top-3 strings in a list by length, but
//           equally identify the top-1 (i.e., best) song by
//           ratings. To help, consider the following definition
//           of an "evaluation" function: one that takes an input
//           of some type and associates an output "score" (where
//           bigger scores are understood to be better):

typealias EvaluationFunction<T> = (T) -> Int

//          Design the function topK that takes a list of
//          items, k (assumed to be a postive integer), and a
//          corresponding evaluation function, and then returns
//          the k items in the list that get the highest score
//          (if there are ties, you are free to return any of the
//          winners; if there aren't enough items in the list,
//          return as many as you can).
//
//          Hint: You did this problem in Homework 7, Problem 1
//                - To simplify, you can avoid the ItemScore type
//                  by using the built-in `zip` function that you
//                  implemented in Homework 7, Problem 3.
//                - Later functions will use topK and assume the
//                  parameter ordering is as described above (which
//                  is a small swap from the sample solution).
//

// combines two lists together
// puts values at the same index into pairs
// is the length of the shorter graph
fun <T, Z> myZip(
    listA: List<T>,
    listB: List<Z>,
): List<Pair<T, Z>> {
    var returnList: MutableList<Pair<T, Z>> = mutableListOf()
    var currentInt = 0
    while (currentInt < listA.count() && currentInt < listB.count()) {
        returnList.add(Pair(listA[currentInt], listB[currentInt]))
        currentInt++
    }
    return returnList
}

@EnabledTest
fun testMyZip() {
    fun <A, B> helpTest(
        listA: List<A>,
        listB: List<B>,
        desc: String,
    ) {
        testSame(
            myZip(listA, listB),
            listA.zip(listB),
            "$desc: a zip b",
        )

        testSame(
            myZip(listB, listA),
            listB.zip(listA),
            "$desc: b zip a",
        )
    }

    helpTest(
        emptyList<String>(),
        emptyList<Int>(),
        "empty/empty",
    )

    helpTest(
        emptyList<String>(),
        listOf(1, 2),
        "empty/non-empty",
    )

    helpTest(
        listOf(1, 2, 3),
        listOf("a", "b"),
        "mixed",
    )
}

// finds the top k elements of a list in terms of a given evaluation
// k needs to be a positive integer
fun <T> topK(
    givenList: List<T>,
    givenEval: EvaluationFunction<T>,
    givenK: Int,
): List<T> {
    val itemPairs: List<Pair<T, Int>> =
        myZip(
            givenList,
            givenList.map({
                    input ->
                givenEval(input)
            }),
        )
    val sortedPair: List<Pair<T, Int>> =
        itemPairs.sortedByDescending(
            { it.second },
        )
    val itemList: List<T> = sortedPair.map({ it.first })
    return itemList.take(givenK)
}

@EnabledTest
fun testTopK() {
    val stringList: List<String> =
        listOf(
            "wow",
            "cool",
            "howdy",
            ":(",
        )
    val intList: List<Int> = listOf(1, 5, 2, 3, -1, -4)
    val emptyStringList: List<String> = listOf()
    val emptyIntList: List<Int> = listOf()
    val getReverseFun: (Int) -> Int = { it * -1 }
    val getLengthFun: (String) -> Int = { it.length }
    testSame(
        topK(stringList, getLengthFun, 4),
        listOf("howdy", "cool", "wow", ":("),
        "String: testFull",
    )
    testSame(
        topK(stringList, getLengthFun, 2),
        listOf("howdy", "cool"),
        "String: testHalf",
    )
    testSame(
        topK(stringList, getLengthFun, 1),
        listOf("howdy"),
        "String: testSingle",
    )
    testSame(
        topK(stringList, getLengthFun, 0),
        listOf(),
        "String: testNone",
    )
    testSame(
        topK(emptyStringList, getLengthFun, 0),
        listOf(),
        "String: testEmptyList",
    )
    testSame(
        topK(emptyStringList, getLengthFun, 4),
        listOf(),
        "String: testEmptyListMoreK",
    )
    testSame(
        topK(intList, getReverseFun, 6),
        listOf(-4, -1, 1, 2, 3, 5),
        "Int: testFull",
    )
    testSame(
        topK(intList, getReverseFun, 3),
        listOf(-4, -1, 1),
        "Int: testHalf",
    )
    testSame(
        topK(intList, getReverseFun, 2),
        listOf(-4, -1),
        "Int: testTwo",
    )
    testSame(
        topK(intList, getReverseFun, 0),
        listOf(),
        "Int: testNone",
    )
    testSame(
        topK(emptyIntList, getReverseFun, 0),
        listOf(),
        "Int: testEmptyList",
    )
    testSame(
        topK(emptyIntList, getReverseFun, 4),
        listOf(),
        "Int: testEmptyListMoreK",
    )
}
// TODO 2/5: Great! Now we have to answer the question from before:
//           what does it mean for two strings to be "close"?
//           There are actually multiple reasonable ways of
//           capturing such a distance, one of which is the
//           Levenshtein Distance, which describes the minimum
//           number of single-character changes (e.g., adding a
//           character, removing one, or substituting) required to
//           change one sequence into another
//           (https://en.wikipedia.org/wiki/Levenshtein_distance).
//           Your task is to design the function
//           levenshteinDistance that computes this distance for
//           two supplied strings.
//
//           Hint: Homework 7, Problem 2 :)
//

// finds the amount of changes between two strings
fun levenshteinDistance(
    a: String,
    b: String,
): Int {
    val aLen: Int = a.length
    val bLen: Int = b.length
    return when {
        (aLen == 0) -> bLen
        (bLen == 0) -> aLen
        (a.first() == b.first()) ->
            levenshteinDistance(
                a.drop(1),
                b.drop(1),
            )
        else ->
            1 +
                minOf(
                    levenshteinDistance(a, b.drop(1)),
                    levenshteinDistance(a.drop(1), b),
                    levenshteinDistance(
                        a.drop(1),
                        b.drop(1),
                    ),
                )
    }
}

@EnabledTest
fun testLevenshteinDistance() {
    testSame(
        levenshteinDistance("", "howdy"),
        5,
        "'', 'howdy'",
    )

    testSame(
        levenshteinDistance("howdy", ""),
        5,
        "'howdy', ''",
    )

    testSame(
        levenshteinDistance("howdy", "howdy"),
        0,
        "'howdy', 'howdy'",
    )

    testSame(
        levenshteinDistance("kitten", "sitting"),
        3,
        "'kitten', 'sitting'",
    )

    testSame(
        levenshteinDistance("sitting", "kitten"),
        3,
        "'sitting', 'kitten'",
    )
}

// TODO 3/5: Great! Now let's design a "k-Nearest Neighbor"
//           classifier (you can read online description, such as
//           on Wikipedia, for lots of details & variants, but
//           we'll give you all the information you need here).
//
//           The goal here: given a dataset of labeled examples,
//           a distance function, and a number k, let the k
//           closest elements of the dataset "vote" (with their
//           label) as to what the label of a new element
//           should be. To be clear, here is a way of describing
//           a distance function, producing a integer distance
//           between two elements of a type...

typealias DistanceFunction<T> = (T, T) -> Int

//           Since this method might give an incorrect response,
//           we'll return not only predicted label, but the number
//           of "votes" received for that label (out of k)...

data class ResultWithVotes<L>(val label: L, val votes: Int)

//           Your task is to uncomment and then *test* the supplied
//           nnLabel function (note: you might need to fix up the
//           ordering of your topK arguments to play nicely with
//           the code here - you should NOT change this function).
//           You'll find guiding comments to help.
//

// uses k-nearest-neighbor (kNN) to predict the label
// for a supplied example given a labeled dataset
// and distance function
fun <E, L> nnLabel(
    queryExample: E,
    dataset: List<LabeledExample<E, L>>,
    distFunc: DistanceFunction<E>,
    k: Int,
): ResultWithVotes<L> {
    // 1. Use topK to find the k-closest dataset elements:
    //    finding the elements whose negated distance is the
    //    greatest is the same as finding those that are closest.
    val closestK =
        topK(dataset, { -distFunc(queryExample, it.example) }, k)

    // 2. Discard the examples, we only care about their labels
    val closestKLabels = closestK.map { it.label }

    // 3. For each distinct label, count up how many time it
    //    showed up in step #2
    //    (Note: once we know the Map type, there are WAY simpler
    //           ways to do this!)
    val labelsWithCounts =
        closestKLabels.distinct().map {
                label ->
            Pair(
                // first = label
                label,
                // second = number of votes
                closestKLabels.filter({ it == label }).size,
            )
        }

    // 4. Use topK to get the label with the greatest count
    val topLabelWithCount = topK(labelsWithCounts, { it.second }, 1)[0]

    // 5. Return both the label and the number of votes (of k)
    return ResultWithVotes(
        topLabelWithCount.first,
        topLabelWithCount.second,
    )
}

@EnabledTest
fun testNNLabel() {
    // don't change this dataset:
    // think of them as points on a line...
    // (with ? referring to the example below)
    //
    //       a   a       ?       b           b
    // |--- --- --- --- --- --- --- --- --- ---|
    //   1   2   3   4   5   6   7   8   9  10
    val dataset =
        listOf(
            LabeledExample(2, "a"),
            LabeledExample(3, "a"),
            LabeledExample(7, "b"),
            LabeledExample(10, "b"),
        )

    // A simple distance: just the absolute value
    fun myAbsVal(
        a: Int,
        b: Int,
    ): Int {
        val diff = a - b

        return when (diff >= 0) {
            true -> diff
            false -> -diff
        }
    }

    // TODO: to demonstrate that you understand how kNN is
    //       supposed to work (and what the supplied code returns),
    //       you are going to write tests here for a selection of
    //       cases that use the dataset and distance function above.
    //
    //       To help you get started, consider testing for point 5,
    //       with k=3:
    //       a) All the points with their distances are...
    //          a = |2 - 5| = 3
    //          a = |3 - 5| = 3
    //          b = |7 - 5| = 2
    //          b = |10 - 5| = 5
    //       b) SO, the labels of the three closest are...
    //          a (2 votes)
    //          b (1 vote)
    //       c) SO, kNN in this situation would predict the label
    //          for this point to be "a", with confidence 2/3 (medium)
    //
    //       We capture this test as...
    //

    testSame(
        nnLabel(5, dataset, ::myAbsVal, k = 3),
        ResultWithVotes("a", 2),
        "NN: 5->a, 2/3",
        // medium confidence
    )

    //       Now your task is to write tests for the following
    //       additional cases...
    //       1. 1 (k=1)
    //       2. 1 (k=2)
    //       3. 10 (k=1)
    //       4. 10 (k=2)
    testSame(
        nnLabel(1, dataset, ::myAbsVal, k = 1),
        ResultWithVotes("a", 1),
        "NN: 1->a, 1/1",
    )
    testSame(
        nnLabel(1, dataset, ::myAbsVal, k = 2),
        ResultWithVotes("a", 2),
        "NN: 1->a, 2/2",
    )
    testSame(
        nnLabel(10, dataset, ::myAbsVal, k = 1),
        ResultWithVotes("b", 1),
        "NN: 10->b, 1/1",
    )
    testSame(
        nnLabel(10, dataset, ::myAbsVal, k = 2),
        ResultWithVotes("b", 2),
        "NN: 10->b, 2/2",
    )
}

// TODO 4/5: Ok - now it's time to put some pieces together!!
//           Finish designing the function yesNoClassifier below -
//           you've been provided with guiding steps, as well as
//           tests that should pass, including those that are
//           incorrect (with lots of confidence!).
//

// we'll generally use k=3 in our classifier
val classifierK = 3

// classifies how confident it is that a string is either positive or negative
// using a dataset
// is not case sensitive
fun yesNoClassifier(s: String): ResultWithVotes<Boolean> {
    // 1. Convert the input to lowercase
    //    (since) the data set is all lowercase
    val loweredInput = s.lowercase()
    // 2. Check to see if the lower-case input
    //    shows up exactly within the dataset
    //    (you can assume there are no duplicates)
    for (i in datasetYN) {
        if (i.example == s) {
            return ResultWithVotes(i.label, classifierK)
        }
    }

    // 3. If the input was found, simply return its label with 100%
    //    confidence (3/3); otherwise, return the result of
    //    performing a 3-NN classification using the dataset and
    //    Levenshtein distance metric.
    return nnLabel(s, datasetYN, ::levenshteinDistance, k = classifierK)
}

@EnabledTest
fun testYesNoClassifier() {
    testSame(
        yesNoClassifier("YES"),
        ResultWithVotes(true, 3),
        "YES: 3/3",
    )

    testSame(
        yesNoClassifier("no"),
        ResultWithVotes(false, 3),
        "no: 3/3",
    )

    testSame(
        yesNoClassifier("nadda"),
        ResultWithVotes(false, 2),
        "nadda: 2/3",
    ) // pretty good ML!

    testSame(
        yesNoClassifier("yerp"),
        ResultWithVotes(true, 3),
        "yerp: 3/3",
    ) // pretty good ML!

    testSame(
        yesNoClassifier("ouch"),
        ResultWithVotes(true, 3),
        "ouch: 3/3",
    ) // seems very confident in this wrong answer...

    testSame(
        yesNoClassifier("now"),
        ResultWithVotes(false, 3),
        "now 3/3",
    ) // seems very confident, given the input doesn't make sense?
}

// TODO 5/5: Now that you have a sense of how this approach works,
//           including some of the (confident) mistakes it can make,
//           uncomment the following lines to have a classifier
//           (that we could use side-by-side with our heuristic).

// classifies whether a string is either positive or negative
// using a dataset
// is not case sensitive
fun isPositiveML(s: String): Boolean = yesNoClassifier(s).label

@EnabledTest
fun testIsPositiveML() {
    // correctly responds with positive (rote memorization)
    for (i in 0..8) {
        helpTestElement(i, true, ::isPositiveML)
    }

    // correctly responds with negative (rote memorization)
    for (i in 9..17) {
        helpTestElement(i, true, ::isPositiveML)
    }
}

// -----------------------------------------------------------------
// Final app!
// -----------------------------------------------------------------

// Whew! You've done a lot :)
//
// Now let's put it together and study!!
//

// TODO 1/2: Design the program studyDeck2 that uses the
//           reactConsole function to study through a
//           supplied deck using a supplied classifier to
//           interpret self-reported correctness.
//
//           The program should produce the following data:
//

// represents the result of a study session:
// how many questions were originally in the deck,
// how many total attempts were required to get
// them all correct!
data class StudyDeckResult(val numQuestions: Int, val numAttempts: Int)

//           Look back to the process you followed for studyDeckr in
//           part 1 of the project: you'll first want to design a
//           state type, then build the main reactConsole function,
//           and finally design all the handlers (and don't forget
//           to test ALL functions, including the program!).
//
//           In case it helps, here's a trace of a short example
//           study session (using the simple classifier), with
//           notes indicated by "<--"
//
//           What is the capital of Massachusetts, USA?
//           Think of the result? Press enter to continue
//                               <-- user just pressed enter, so ""
//           Boston
//           Correct? (Y)es/(N)o
//           yup
//           What is the capital of California, USA?
//           Think of the result? Press enter to continue
//
//           Sacramento
//           Correct? (Y)es/(N)o
//           no :(                     <-- cycles Cali to the back!
//           What is the capital of the United Kingdom?
//           Think of the result? Press enter to continue
//
//           London
//           Correct? (Y)es/(N)o
//           YES!
//           What is the capital of California, USA?
//           Think of the result? Press enter to continue
//
//           Sacramento
//           Correct? (Y)es/(N)o
//           yessir!
//           Questions: 3, Attempts: 4 <-- useful summary of return
//

// stores the current amount of attempts, the original amount of questions
data class StudyDeckState(val studyResults: StudyDeckResult, val deckState: IDeck)

// some example values
val taggedCardFullState = StudyDeckState(StudyDeckResult(4, 1), fullDeck)
val taggedCardHalfState = StudyDeckState(StudyDeckResult(1, 0), halfDeck)
val taggedCardEmptyState = StudyDeckState(StudyDeckResult(3, 5), emptyDeck)
val squareLongState = StudyDeckState(StudyDeckResult(5, 0), longSquaresDeck)
val squareShortState = StudyDeckState(StudyDeckResult(5, 5), shortSquaresDeck)
val squareEmptyState = StudyDeckState(StudyDeckResult(0, 0), emptySquaresDeck)

// Some useful prompts
val studyThink = "Think of the result? Press enter to continue"
val studyCheck = "Correct? (Y)es/(N)o"

// Describes amount of correct anwsers, for reactconsole
fun finalStateText(currentState: StudyDeckState): String {
    return "Questions: ${currentState.studyResults.numQuestions}, Attempts: ${currentState.studyResults.numAttempts}"
}

@EnabledTest
fun testFinalStateText() {
    testSame(
        finalStateText(taggedCardFullState),
        "Questions: 4, Attempts: 1",
        "Test flash card full state",
    )
    testSame(
        finalStateText(taggedCardHalfState),
        "Questions: 1, Attempts: 0",
        "Test flash card half state",
    )
    testSame(
        finalStateText(taggedCardEmptyState),
        "Questions: 3, Attempts: 5",
        "Test flash card state",
    )
    testSame(
        finalStateText(squareLongState),
        "Questions: 5, Attempts: 0",
        "Test squares long state",
    )
    testSame(
        finalStateText(squareShortState),
        "Questions: 5, Attempts: 5",
        "Test squares short state",
    )
    testSame(
        finalStateText(squareEmptyState),
        "Questions: 0, Attempts: 0",
        "Test squares empty state",
    )
}

// Gets the text repersentation of a current state of the study deck
fun deckStateToText(usedDeckState: StudyDeckState): String {
    val gottenText: String = usedDeckState.deckState.getText() ?: ""
    return when (usedDeckState.deckState.getState()) {
        DeckState.QUESTION -> "$gottenText $studyThink"
        DeckState.ANSWER -> "$gottenText $studyCheck"
        else -> ""
    }
}

@EnabledTest
fun testDeckStateToText() {
    testSame(
        deckStateToText(taggedCardFullState),
        "What is the capital of the U.S.A $studyThink",
        "Full Deck Test",
    )
    testSame(
        deckStateToText(taggedCardHalfState),
        "10 $studyCheck",
        "Half Deck Test",
    )
    testSame(
        deckStateToText(taggedCardEmptyState),
        "",
        "Empty Deck Test",
    )
    testSame(
        deckStateToText(squareLongState),
        "1 * 1 = ? $studyThink",
        "Long Squares Test",
    )
    testSame(
        deckStateToText(squareShortState),
        "4 $studyCheck",
        "Short Squares Test",
    )
    testSame(
        deckStateToText(squareEmptyState),
        "",
        "Empty Squares Test",
    )
}

// Tells reactconsole if study session has finished
fun hasFinishedSession(currentState: StudyDeckState): Boolean {
    return (currentState.deckState.getState() == DeckState.EXHAUSTED)
}

@EnabledTest
fun testHasFinishedSession() {
    testSame(
        hasFinishedSession(taggedCardFullState),
        false,
        "Test flash card full state",
    )
    testSame(
        hasFinishedSession(taggedCardHalfState),
        false,
        "Test flash card half state",
    )
    testSame(
        hasFinishedSession(taggedCardEmptyState),
        true,
        "Test flash card empty state",
    )
    testSame(
        hasFinishedSession(squareLongState),
        false,
        "Test squares long state",
    )
    testSame(
        hasFinishedSession(squareShortState),
        false,
        "Test squares short state",
    )
    testSame(
        hasFinishedSession(squareEmptyState),
        true,
        "Test squares empty state",
    )
}

// Cycles through a deck, having the
// user report their result on each anwser
// returning the amount of attempts and total questions in the end
fun studyDeck2(
    givenDeck: IDeck,
    affirmationChecker: (String) -> Boolean,
): StudyDeckResult {
    val startingStudyState: StudyDeckState = StudyDeckState(StudyDeckResult(givenDeck.getSize(), 0), givenDeck)

    // Moves from state to state depending on input for reactconsole,
    // Cycles cards to the back of deck depending on whether
    // user self reports positive result or not
    fun transitionState(
        currentState: StudyDeckState,
        input: String,
    ): StudyDeckState {
        return when (currentState.deckState.getState()) {
            DeckState.QUESTION -> return StudyDeckState(
                currentState.studyResults,
                currentState.deckState.flip(),
            )
            DeckState.ANSWER ->
                if (affirmationChecker(input)) {
                    return StudyDeckState(
                        StudyDeckResult(
                            currentState.studyResults.numQuestions,
                            currentState.studyResults.numAttempts + 1,
                        ),
                        currentState.deckState.next(true),
                    )
                } else {
                    return StudyDeckState(
                        StudyDeckResult(
                            currentState.studyResults.numQuestions,
                            currentState.studyResults.numAttempts + 1,
                        ),
                        currentState.deckState.next(false),
                    )
                }
            else -> return StudyDeckState(
                currentState.studyResults,
                currentState.deckState,
            )
        }
    }

    return reactConsole(
        initialState = startingStudyState,
        stateToText = ::deckStateToText,
        nextState = ::transitionState,
        isTerminalState = ::hasFinishedSession,
        terminalStateToText = ::finalStateText,
    ).studyResults
}

@EnabledTest
fun testStudydeck2() {
    // Creates a function with a deck built in for captureResult
    fun createTestFun(
        currentDeck: IDeck,
        givenAffirmationChecker: (String) -> Boolean,
    ): () -> StudyDeckResult {
        fun createDeck(): StudyDeckResult {
            return studyDeck2(currentDeck, givenAffirmationChecker)
        }
        return ::createDeck
    }

    // Uses ML
    testSame(
        captureResults(
            createTestFun(fullDeck, ::isPositiveML),
            "",
            "Yeas",
            "",
            "false",
            "affirm",
            "affirm",
            "aeof",
            "yoss",
        ),
        CapturedResult(
            StudyDeckResult(3, 4),
            "${problem1.frontText} $studyThink",
            "${problem1.backText} $studyCheck",
            "${problem2.frontText} $studyThink",
            "${problem2.backText} $studyCheck",
            "${problem3.frontText} $studyThink",
            "${problem3.backText} $studyCheck",
            "${problem2.frontText} $studyThink",
            "${problem2.backText} $studyCheck",
            "Questions: 3, Attempts: 4",
        ),
        "ML Classifier: test flash card full deck",
    )
    testSame(
        captureResults(
            createTestFun(halfDeck, ::isPositiveML),
            "aye",
            "yoss",
            "",
        ),
        CapturedResult(
            StudyDeckResult(1, 1),
            "${problem2.backText} $studyCheck",
            "Questions: 1, Attempts: 1",
        ),
        "ML Classifier: test flash card half deck",
    )
    testSame(
        captureResults(
            createTestFun(emptyDeck, ::isPositiveML),
            "wfew",
            "gooba",
        ),
        CapturedResult(
            StudyDeckResult(0, 0),
            "Questions: 0, Attempts: 0",
        ),
        "ML Classifier: test flash card empty deck",
    )
    testSame(
        captureResults(
            createTestFun(
                longSquaresDeck,
                ::isPositiveML,
            ),
            "NOW",
            "false",
            "nahh",
            "N",
            "No",
            "N",
            "nah",
            "N",
            "N",
            "N",
            "yes",
            "yes",
            "yes",
            "roger",
            "YO",
            "yes",
            "Affirmative",
            "aye",
            "yeah",
            "YES",
            "YE",
        ),
        CapturedResult(
            StudyDeckResult(5, 10),
            "1 * 1 = ? $studyThink",
            "1 $studyCheck",
            "2 * 2 = ? $studyThink",
            "4 $studyCheck",
            "3 * 3 = ? $studyThink",
            "9 $studyCheck",
            "4 * 4 = ? $studyThink",
            "16 $studyCheck",
            "5 * 5 = ? $studyThink",
            "25 $studyCheck",
            "1 * 1 = ? $studyThink",
            "1 $studyCheck",
            "2 * 2 = ? $studyThink",
            "4 $studyCheck",
            "3 * 3 = ? $studyThink",
            "9 $studyCheck",
            "4 * 4 = ? $studyThink",
            "16 $studyCheck",
            "5 * 5 = ? $studyThink",
            "25 $studyCheck",
            "Questions: 5, Attempts: 10",
        ),
        "ML Classifier: test squares long deck",
    )
    testSame(
        captureResults(
            createTestFun(shortSquaresDeck, ::isPositiveML),
            "nAH",
            "nah",
            "Yes",
            "yes",
        ),
        CapturedResult(
            StudyDeckResult(1, 2),
            "4 $studyCheck",
            "2 * 2 = ? $studyThink",
            "4 $studyCheck",
            "Questions: 1, Attempts: 2",
        ),
        "ML Classifier: test squares short deck",
    )
    testSame(
        captureResults(
            createTestFun(emptySquaresDeck, ::isPositiveML),
            "wer",
            "weob",
        ),
        CapturedResult(
            StudyDeckResult(0, 0),
            "Questions: 0, Attempts: 0",
        ),
        "ML Classifier: test squares empty deck",
    )

    // Uses simple
    testSame(
        captureResults(
            createTestFun(fullDeck, ::isPositiveSimple),
            "",
            "Yeas",
            "",
            "n",
            "yaffirm",
            "yaffirm",
            "yaeof",
            "yoss",
        ),
        CapturedResult(
            StudyDeckResult(3, 4),
            "${problem1.frontText} $studyThink",
            "${problem1.backText} $studyCheck",
            "${problem2.frontText} $studyThink",
            "${problem2.backText} $studyCheck",
            "${problem3.frontText} $studyThink",
            "${problem3.backText} $studyCheck",
            "${problem2.frontText} $studyThink",
            "${problem2.backText} $studyCheck",
            "Questions: 3, Attempts: 4",
        ),
        "Simple Classifier: test flash card full deck",
    )
    testSame(
        captureResults(
            createTestFun(halfDeck, ::isPositiveSimple),
            "Y",
            "yoswioefs",
            "",
        ),
        CapturedResult(
            StudyDeckResult(1, 1),
            "${problem2.backText} $studyCheck",
            "Questions: 1, Attempts: 1",
        ),
        "Simple Classifier: test flash card half deck",
    )
    testSame(
        captureResults(
            createTestFun(emptyDeck, ::isPositiveSimple),
            "wfew",
            "gooba",
        ),
        CapturedResult(
            StudyDeckResult(0, 0),
            "Questions: 0, Attempts: 0",
        ),
        "Simple Classifier: test flash card empty deck",
    )
    testSame(
        captureResults(
            createTestFun(
                longSquaresDeck,
                ::isPositiveSimple,
            ),
            "NW",
            "nah",
            "nahh",
            "N",
            "No",
            "NAH",
            "nah",
            "N",
            "N",
            "N",
            "yes",
            "yes",
            "yes",
            "Y",
            "YO",
            "yes",
            "Yffirmative",
            "yAffirmations",
            "yeah",
            "YES",
            "YE",
        ),
        CapturedResult(
            StudyDeckResult(5, 10),
            "1 * 1 = ? $studyThink",
            "1 $studyCheck",
            "2 * 2 = ? $studyThink",
            "4 $studyCheck",
            "3 * 3 = ? $studyThink",
            "9 $studyCheck",
            "4 * 4 = ? $studyThink",
            "16 $studyCheck",
            "5 * 5 = ? $studyThink",
            "25 $studyCheck",
            "1 * 1 = ? $studyThink",
            "1 $studyCheck",
            "2 * 2 = ? $studyThink",
            "4 $studyCheck",
            "3 * 3 = ? $studyThink",
            "9 $studyCheck",
            "4 * 4 = ? $studyThink",
            "16 $studyCheck",
            "5 * 5 = ? $studyThink",
            "25 $studyCheck",
            "Questions: 5, Attempts: 10",
        ),
        "Simple Classifier: test squares long deck",
    )
    testSame(
        captureResults(
            createTestFun(shortSquaresDeck, ::isPositiveSimple),
            "nAH",
            "nah",
            "Yes",
            "yes",
        ),
        CapturedResult(
            StudyDeckResult(1, 2),
            "4 $studyCheck",
            "2 * 2 = ? $studyThink",
            "4 $studyCheck",
            "Questions: 1, Attempts: 2",
        ),
        "Simple Classifier: test squares short deck",
    )
    testSame(
        captureResults(
            createTestFun(emptySquaresDeck, ::isPositiveSimple),
            "wer",
            "weob",
        ),
        CapturedResult(
            StudyDeckResult(0, 0),
            "Questions: 0, Attempts: 0",
        ),
        "Simple Classifier: test squares empty deck",
    )
}

// TODO 2/2: Finally, design the program study2 that...
//           a) Uses chooseMenuOption to select from amongst a
//              list of decks; the options must include at least
//              one deck read from a file (using
//              readTaggedFlashCardsFile), one generated by code
//              (using PerfectSquaresDeck), and one that filters
//              based upon a tag being present (e.g., only
//              "hard" cards from a list; this may be the cards
//              read from a file).
//           b) If the menu in (a) didn't result in quitting, then
//              uses chooseMenuOption again to select from amongst
//              the two sentiment analysis functions.
//           c) If the menu in (b) didn't result in quitting, then
//              uses studyDeck2 to study through the selected deck
//              with the selected sentiment analysis function.
//           d) Returns to (a) and continues until either of the
//              two menus indicate a desire to quit.
//
//           Make sure to provide tests that capture (at least)...
//           - Quitting at the selection of decks
//           - Quitting at the selection of sentiment analysis
//             functions
//           - Studying through at least one deck
//

// some useful labels
val optSimple = "Simple Self-Report Evaluation"
val optML = "ML Self-Report Evaluation"

fun study2() {
    val deckOptions: List<NamedMenuOption<IDeck>> =
        listOf(
            NamedMenuOption<IDeck>(fullDeck, "General Problems Deck"),
            NamedMenuOption<IDeck>(longSquaresDeck, "Perfect Squares Deck"),
            NamedMenuOption<IDeck>(
                TFCListDeck(
                    readTaggedFlashCardsFile(exampleAddress).filter({
                        it.isTagged("hard")
                    }),
                    DeckState.QUESTION,
                ),
                "Hard Tagged Cards From File Deck",
            ),
        )
    do {
        val deckPossible: NamedMenuOption<IDeck>? = chooseMenuOption(deckOptions)
        if (deckPossible != null) {
            val sentimentOptions: List<NamedMenuOption<PositivityClassifier>> =
                listOf<NamedMenuOption<PositivityClassifier>>(
                    NamedMenuOption<PositivityClassifier>(
                        ::isPositiveSimple,
                        optSimple,
                    ),
                    NamedMenuOption<PositivityClassifier>(
                        ::isPositiveML,
                        optML,
                    ),
                )
            val checkPossible = chooseMenuOption(sentimentOptions)
            if (checkPossible != null) {
                studyDeck2(deckPossible.option, checkPossible.option)
            } else {
                return
            }
        } else {
            return
        }
    }
    while (true)
}

@EnabledTest
fun testStudy2() {
    testSame(
        captureResults(::study2, "0"),
        CapturedResult(
            Unit,
            "1. General Problems Deck",
            "2. Perfect Squares Deck",
            "3. Hard Tagged Cards From File Deck",
            "",
            menuPrompt,
            menuQuit,
        ),
        "Quit on Deck Selection Test",
    )
    testSame(
        captureResults(::study2, "3", "0"),
        CapturedResult(
            Unit,
            "1. General Problems Deck",
            "2. Perfect Squares Deck",
            "3. Hard Tagged Cards From File Deck",
            "",
            menuPrompt,
            "${menuChoicePrefix}Hard Tagged Cards From File Deck",
            "1. $optSimple",
            "2. $optML",
            "",
            menuPrompt,
            menuQuit,
        ),
        "Quit on Sentiment Analysis Chooser Test",
    )
    testSame(
        captureResults(
            ::study2,
            "1",
            "1",
            "yNo",
            "yNo",
            "yNo",
            "Nyes",
            "yNo",
            "yNo",
            "yNo",
            "yNo",
            "0",
        ),
        CapturedResult(
            Unit,
            "1. General Problems Deck",
            "2. Perfect Squares Deck",
            "3. Hard Tagged Cards From File Deck",
            "",
            menuPrompt,
            "${menuChoicePrefix}General Problems Deck",
            "1. $optSimple",
            "2. $optML",
            "",
            menuPrompt,
            "$menuChoicePrefix$optSimple",
            "${problem1.frontText} $studyThink",
            "${problem1.backText} $studyCheck",
            "${problem2.frontText} $studyThink",
            "${problem2.backText} $studyCheck",
            "${problem3.frontText} $studyThink",
            "${problem3.backText} $studyCheck",
            "${problem2.frontText} $studyThink",
            "${problem2.backText} $studyCheck",
            "Questions: 3, Attempts: 4",
            "1. General Problems Deck",
            "2. Perfect Squares Deck",
            "3. Hard Tagged Cards From File Deck",
            "",
            menuPrompt,
            menuQuit,
        ),
        "Cycle through a Deck Test",
    )
}
// -----------------------------------------------------------------

fun main() {
//  study2()
}

runEnabledTests(this)
main()
