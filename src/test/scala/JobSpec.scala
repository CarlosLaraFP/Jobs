import com.valinor.jobs.ErrorTypes._
import com.valinor.jobs._
import com.valinor.jobs.JobTypes._
import zio._
import zio.test.TestAspect._
import zio.test._


object JobSpec extends ZIOSpecDefault {
  // Property-based tests prioritized

  private val baseTest = test("Addition associativity") {
    // 100 examples each generator by default
    check(Gen.int, Gen.int, Gen.int) { (x, y, z) =>
      // Statement must be true for all x, y, z
      assertTrue(
        ((x + y) + z) == (x + (y + z))
      )
    }
  }

  private val jobCreationSuccess = test("Job.createFromRequest success") {
    // CreateJobRequest apply method with 5 Gens
    val titleGenerator = Gen.stringBounded(1, 50)(Gen.alphaNumericChar)
    val descriptionGenerator = Gen.option(Gen.stringBounded(1, 250)(Gen.alphaNumericChar))
    val rateGenerator = Gen.double(7.26, 300.0)
    val nameGenerator = Gen.stringBounded(1, 35)(Gen.alphaNumericChar)
    val sizeGenerator = Gen.int(1, 10000)

    check(titleGenerator, descriptionGenerator, rateGenerator, nameGenerator, sizeGenerator) { (title, description, rate, name, size) =>
      Job.createFromRequest(
        CreateJobRequest(title, description, rate, name, size)
      ).map { job: Job =>
        assertTrue(job.status == JobStatus.Created)
      }
    }
  }

  private val jobCreationJobTitleFailure = test("Job.createFromRequest JobTitle failure") {
    // JobTitle must fail validation
    val titleGenerator = Gen.oneOf(Gen.const(""), Gen.stringBounded(51, 150)(Gen.alphaNumericChar))
    val descriptionGenerator = Gen.option(Gen.stringBounded(1, 250)(Gen.alphaNumericChar))
    val rateGenerator = Gen.double(7.26, 300.0)
    val nameGenerator = Gen.stringBounded(1, 35)(Gen.alphaNumericChar)
    val sizeGenerator = Gen.int(1, 10000)

    check(titleGenerator, descriptionGenerator, rateGenerator, nameGenerator, sizeGenerator) { (title, description, rate, name, size) =>
      Job.createFromRequest(
        CreateJobRequest(title, description, rate, name, size)
      ).either.map { (r: Either[PostRequestError, Job]) =>
        assertTrue(
          r.isLeft && PostRequestError.unwrap(r.left.getOrElse(PostRequestError(""))) == "Job title must be between 1 and 50 characters"
        )
      }
    }
  }

  // TODO: Unit tests

  // TODO: Integration tests
  /*
    An integration test is a type of test that checks how multiple parts of a system work together.
    The test below interacts with the DatabaseService, which interacts with a PostgreSQL database
    to perform the main case management operations.
    This test verifies that all of these interactions with the database service are working as expected.
   */


  override def spec =
    suite("Property-based tests")(
      baseTest,
      jobCreationSuccess,
      jobCreationJobTitleFailure
    ).provide(
      JobService.live,
      InMemoryDatabase.live
    ) @@ timed @@ samples(250) // @@ sequential
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
