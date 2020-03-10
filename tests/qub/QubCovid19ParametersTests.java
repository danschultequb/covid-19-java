package qub;

public interface QubCovid19ParametersTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(QubCovid19Parameters.class, () ->
        {
            runner.testGroup("constructor()", () ->
            {
                runner.test("with null output", (Test test) ->
                {
                    final CharacterWriteStream output = null;
                    final HttpClient httpClient = HttpClient.create(test.getNetwork());
                    final DateTime now = DateTime.create(2020, 1, 1);

                    test.assertThrows(() -> new QubCovid19Parameters(output, httpClient, now),
                        new PreConditionFailure("output cannot be null."));
                });

                runner.test("with null httpClient", (Test test) ->
                {
                    final CharacterWriteStream output = new InMemoryCharacterStream();
                    final HttpClient httpClient = null;
                    final DateTime now = DateTime.create(2020, 1, 1);

                    test.assertThrows(() -> new QubCovid19Parameters(output, httpClient, now),
                        new PreConditionFailure("httpClient cannot be null."));
                });

                runner.test("with null now", (Test test) ->
                {
                    final CharacterWriteStream output = new InMemoryCharacterStream();
                    final HttpClient httpClient = HttpClient.create(test.getNetwork());
                    final DateTime now = null;

                    test.assertThrows(() -> new QubCovid19Parameters(output, httpClient, now),
                        new PreConditionFailure("now cannot be null."));
                });

                runner.test("with valid arguments", (Test test) ->
                {
                    final CharacterWriteStream output = new InMemoryCharacterStream();
                    final HttpClient httpClient = HttpClient.create(test.getNetwork());
                    final DateTime now = DateTime.create(2020, 1, 1);

                    final QubCovid19Parameters parameters = new QubCovid19Parameters(output, httpClient, now);
                    test.assertSame(output, parameters.getOutput());
                    test.assertSame(httpClient, parameters.getHttpClient());
                    test.assertSame(now, parameters.getNow());
                });
            });
        });
    }
}
