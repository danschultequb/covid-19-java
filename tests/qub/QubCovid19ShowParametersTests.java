package qub;

public interface QubCovid19ShowParametersTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(QubCovid19ShowParameters.class, () ->
        {
            runner.testGroup("constructor()", () ->
            {
                runner.test("with null output", (Test test) ->
                {
                    final CharacterToByteWriteStream output = null;
                    final VerboseCharacterToByteWriteStream verbose = VerboseCharacterToByteWriteStream.create(InMemoryCharacterToByteStream.create())
                        .setIsVerbose(false);
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create(test.getClock());
                    final Folder dataFolder = fileSystem.getFolder("/data/").await();
                    final Covid19DataSource dataSource = Covid19InMemoryDataSource.create();

                    test.assertThrows(() -> new QubCovid19ShowParameters(output, verbose, dataFolder, dataSource),
                        new PreConditionFailure("output cannot be null."));
                });

                runner.test("with null dataSource", (Test test) ->
                {
                    final CharacterToByteWriteStream output = InMemoryCharacterToByteStream.create();
                    final VerboseCharacterToByteWriteStream verbose = VerboseCharacterToByteWriteStream.create(output)
                        .setIsVerbose(false);
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create(test.getClock());
                    final Folder dataFolder = fileSystem.getFolder("/data/").await();
                    final Covid19DataSource dataSource = null;

                    test.assertThrows(() -> new QubCovid19ShowParameters(output, verbose, dataFolder, dataSource),
                        new PreConditionFailure("dataSource cannot be null."));
                });

                runner.test("with valid arguments", (Test test) ->
                {
                    final CharacterToByteWriteStream output = InMemoryCharacterToByteStream.create();
                    final VerboseCharacterToByteWriteStream verbose = VerboseCharacterToByteWriteStream.create(output)
                        .setIsVerbose(false);
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create(test.getClock());
                    final Folder dataFolder = fileSystem.getFolder("/data/").await();
                    final Covid19DataSource dataSource = Covid19InMemoryDataSource.create();

                    final QubCovid19ShowParameters parameters = new QubCovid19ShowParameters(output, verbose, dataFolder, dataSource);
                    test.assertSame(output, parameters.getOutput());
                    test.assertSame(dataSource, parameters.getDataSource());
                });
            });
        });
    }
}
