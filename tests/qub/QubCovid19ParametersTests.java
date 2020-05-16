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
                    final VerboseCharacterWriteStream verbose = new VerboseCharacterWriteStream(false, InMemoryCharacterStream.create());
                    final Covid19DataSource dataSource = Covid19InMemoryDataSource.create();

                    test.assertThrows(() -> new QubCovid19Parameters(output, verbose, dataSource),
                        new PreConditionFailure("output cannot be null."));
                });

                runner.test("with null dataSource", (Test test) ->
                {
                    final CharacterWriteStream output = InMemoryCharacterStream.create();
                    final VerboseCharacterWriteStream verbose = new VerboseCharacterWriteStream(false, output);
                    final Covid19DataSource dataSource = null;

                    test.assertThrows(() -> new QubCovid19Parameters(output, verbose, dataSource),
                        new PreConditionFailure("dataSource cannot be null."));
                });

                runner.test("with valid arguments", (Test test) ->
                {
                    final CharacterWriteStream output = InMemoryCharacterStream.create();
                    final VerboseCharacterWriteStream verbose = new VerboseCharacterWriteStream(false, output);
                    final Covid19DataSource dataSource = Covid19InMemoryDataSource.create();

                    final QubCovid19Parameters parameters = new QubCovid19Parameters(output, verbose, dataSource);
                    test.assertSame(output, parameters.getOutput());
                    test.assertSame(dataSource, parameters.getDataSource());
                });
            });
        });
    }
}
