import com.valinor.jobs._
import com.valinor.jobs.JobTypes._

import zio._
import zio.test.TestAspect._
import zio.test._
import java.util.UUID


object JobSpec extends ZIOSpecDefault {

  // TODO: Unit tests

/*
  val deleteTableTest: Spec[DatabaseService, RequestError] =
    test("databaseService.modifyTable with Delete") {
      val effect = for {
        dbService <- ZIO.service[DatabaseService]
        mutation = ModifyTable(TableAction.Delete)
        result <- dbService.modifyTable(mutation)
      } yield result.result

      assertZIO(effect) {
        Assertion.assertion("RDS PostgreSQL DB table deleted") {
          _.contains("success")
        }
      }
    }
*/

  // TODO: Integration tests
  /*
    An integration test is a type of test that checks how multiple parts of a system work together.
    The test below interacts with the DatabaseService, which interacts with a PostgreSQL database
    to perform the main case management operations.
    This test verifies that all of these interactions with the database service are working as expected.
   */

  // TODO: Property-based tests
/*
  val pbtCreateCase: Spec[DatabaseService, RequestError] =
    test("PBT: databaseService.createCase") {

      val nameGenerator = Gen.stringBounded(1, 99)(Gen.char)

      val dobGenerator = for {
        year <- Gen.stringN(4)(Gen.numericChar)
        month <- Gen.fromIterable(1 to 12).map(i => if (i < 10) s"0$i" else s"$i")
        day <- Gen.fromIterable(1 to 28).map(i => if (i < 10) s"0$i" else s"$i")
      } yield s"$year-$month-$day"

      val dodGenerator = Gen.option(dobGenerator)

      check(nameGenerator, dobGenerator, dodGenerator) { (name, dob, dod) =>
        val effect = for {
          dbService <- ZIO.service[DatabaseService]
          create = CreateCase(name, dob, dod)
          result <- dbService.createCase(create)
        } yield result

        effect.map(m =>
          assertTrue(
            m.result.nonEmpty &&
              m.caseId.nonEmpty &&
              m.caseStatus.get == CaseStatus.Pending
          )
        )
      }
    }
*/

  // TODO: Failure test cases

  override def spec =
    suite("WebsiteSpec")(
/*
      suite("DatabaseService ZLayer")(
        createTableTest,
        suite("")(
          caseLifecycleTest,
          clearTableTest
        ) @@ nonFlaky(3),
        pbtCreateCase @@ nonFlaky(3),
        invalidDateFailure,
        deleteTableTest
      ).provide(
        DatabaseService.live,
        ZLayer.fromZIO(
          Hub.unbounded[CaseStatusChanged]
        ),
        PostgresTransactor.live(
        "org.postgresql.Driver",
        "jdbc:postgresql://localhost:5432/casesdb",
        "postgres",
        "postgres"
        )
      ) @@ sequential,
*/
      test("Property-Based Testing") {
        // 100 examples each generator by default
        check(Gen.int, Gen.int, Gen.int) { (x, y, z) =>
          // Statement must be true for all x, y, z, ...
          assertTrue(((x + y) + z) == (x + (y + z)))
        }
      }
    ) @@ timed
}
/*
  TODO: Assertion variants
    - Assertion.assertion => tests any truth value (the most general assertion)
    - Assertion.equalTo => tests for equality
    - Assertion.fails/failsCause => expects effect to fail with the exact (typed) failure/cause specified
    - Assertion.dies => expects effect to fail with a Throwable that was not part of the type signature (defects)
    - Assertion.isInterrupted => validates an interruption on the effect
    - Specialized:
      - isLeft/isRight for Either
      - isSome/isNone for Option
      - isSuccess/isFailure for Try
      - isEmpty/isNonEmpty/contains/hasSize/has* for Iterable
      - isEmptyString/nonEmptyString/startsWithString/containsString/matchesRegex for String
      - isLessThan/isGreaterThan
 */
/*
  TODO: Aspects
    - timeout(duration)
    - eventually - retries a test until success
    - flaky(retries) - flaky tests with a limit
    - nonFlaky(n) - repeats n times, stops at first failure
    - repeats(n) - same
    - retries(n) - retries n times, stops at first success
    - debug - prints everything it can to the console
    - silent - prints nothing
    - diagnose(duration) - timeout with fiber dump explaining what happened
    - parallel/sequential (aspects of a suite, not a single test)
    - ignore - skips test(s)
    - success - fail all ignored tests
    - timed - measure execution time
    - before/beforeAll + after/afterAll
 */
/*
  TODO: [Gen]erators
    - int
    - char, alphaChar, alphaNumericChar, asciiChar, hexChar, printableChar
    - string, stringN
    - const
    - elements
    - fromIterable(n to m)
    - uniform - select doubles between 0 and 1
    - fromRandom(...)
    - fromZIO(...)
    - unfoldGen
    - Specialized:
      - listOf
      - setOfN
      - option
      - either
    - Combinators:
      - generator.zip
      - generator.map(...)
      - generator.filter(...)
      - generator.flatMap(...)
    - for-comprehensions
 */
