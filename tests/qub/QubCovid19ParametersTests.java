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
                    final Covid19DataSource dataSource = Covid19InMemoryDataSource.create();

                    test.assertThrows(() -> new QubCovid19Parameters(output, dataSource),
                        new PreConditionFailure("output cannot be null."));
                });

                runner.test("with null dataSource", (Test test) ->
                {
                    final CharacterWriteStream output = InMemoryCharacterStream.create();
                    final Covid19DataSource dataSource = null;

                    test.assertThrows(() -> new QubCovid19Parameters(output, dataSource),
                        new PreConditionFailure("dataSource cannot be null."));
                });

                runner.test("with valid arguments", (Test test) ->
                {
                    final CharacterWriteStream output = InMemoryCharacterStream.create();
                    final Covid19DataSource dataSource = Covid19InMemoryDataSource.create();

                    final QubCovid19Parameters parameters = new QubCovid19Parameters(output, dataSource);
                    test.assertSame(output, parameters.getOutput());
                    test.assertSame(dataSource, parameters.getDataSource());
                });
            });
        });
    }
}
