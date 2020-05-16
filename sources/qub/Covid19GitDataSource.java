package qub;

public class Covid19GitDataSource implements Covid19DataSource
{
    private static final String githubRepositoryUrl = "https://github.com/CSSEGISandData/COVID-19.git";
    private static final String gitRepositoryName = "COVID-19";

    private final Git git;
    private final Folder gitRepositoryFolder;
    private final CharacterWriteStream verbose;

    private Covid19GitDataSource(Folder projectDataFolder, Git git, CharacterWriteStream verbose)
    {
        PreCondition.assertNotNull(projectDataFolder, "projectDataFolder");
        PreCondition.assertNotNull(git, "git");
        PreCondition.assertNotNull(verbose, "verbose");

        this.git = git;
        this.gitRepositoryFolder = projectDataFolder.getFolder(Covid19GitDataSource.gitRepositoryName).await();
        this.verbose = verbose;
    }

    public static Covid19GitDataSource create(Folder projectDataFolder, Git git, CharacterWriteStream verbose)
    {
        return new Covid19GitDataSource(projectDataFolder, git, verbose);
    }

    @Override
    public Result<Void> refreshData()
    {
        return Result.create(() ->
        {
            if (this.gitRepositoryFolder.exists().await())
            {
                this.git.getPullProcessBuilder().await()
                    .setWorkingFolder(this.gitRepositoryFolder)
                    .setVerbose(this.verbose)
                    .run().await();
            }
            else
            {
                this.git.getCloneProcessBuilder(Covid19GitDataSource.githubRepositoryUrl).await()
                    .setDirectory(this.gitRepositoryFolder)
                    .setVerbose(this.verbose)
                    .run().await();
            }
        });
    }

    @Override
    public Result<Covid19Summary> getDataSummary()
    {
        return Result.create(() ->
        {
            final Covid19Summary result = Covid19Summary.create();

            final Iterable<File> dailyReportFiles = this.getDailyReportFiles().await();
            result.setDatesReportedCount(dailyReportFiles.getCount());

            final File mostRecentDailyReportFile = Covid19GitDataSource.getMostRecentDailyReportFile(dailyReportFiles);
            result.setMostRecentDateReported(Covid19GitDataSource.parseDailyReportFileDate(mostRecentDailyReportFile).await());

            final Covid19DailyReport mostRecentDailyReport = Covid19DailyReport.parse(mostRecentDailyReportFile).await();
            result.setCountriesReportedCount(mostRecentDailyReport.getDataRows()
                .map(Covid19DailyReportDataRow::getCountryOrRegion)
                .toSet()
                .getCount());

            return result;
        });
    }

    private Result<Iterable<File>> getDailyReportFiles()
    {
        return Result.create(() ->
        {
            final Folder dailyReportsFolder = this.gitRepositoryFolder.getFolder("csse_covid_19_data/csse_covid_19_daily_reports/").await();
            final Iterable<File> result = dailyReportsFolder.getFiles().await()
                .where((File dailyReportFile) -> Comparer.equal(dailyReportFile.getFileExtension(), ".csv"))
                .toList();

            PostCondition.assertNotNull(result, "result");

            return result;
        });
    }

    private Result<File> getMostRecentDailyReportFile()
    {
        return Result.create(() ->
        {
            final Iterable<File> dailyReportFiles = this.getDailyReportFiles().await();
            return Covid19GitDataSource.getMostRecentDailyReportFile(dailyReportFiles);
        });
    }

    private static File getMostRecentDailyReportFile(Iterable<File> dailyReportFiles)
    {
        PreCondition.assertNotNull(dailyReportFiles, "dailyReportFiles");

        return dailyReportFiles.maximum((File lhs, File rhs) ->
        {
            final DateTime lhsDate = Covid19GitDataSource.parseDailyReportFileDate(lhs).catchError().await();
            final DateTime rhsDate = Covid19GitDataSource.parseDailyReportFileDate(rhs).catchError().await();
            return Comparer.compare(lhsDate, rhsDate);
        });
    }

    /**
     * Parse the date for the provided daily report file.
     * @param dailyReportFile The daily report file to get the date of.
     * @return The date of the provided daily report file.
     */
    private static Result<DateTime> parseDailyReportFileDate(File dailyReportFile)
    {
        PreCondition.assertNotNull(dailyReportFile, "dailyReportFile");

        return Result.create(() ->
        {
            final String[] dateParts = dailyReportFile.getNameWithoutFileExtension().split("-");
            final int month = Integers.parse(dateParts[0]).await();
            final int day = Integers.parse(dateParts[1]).await();
            final int year = Integers.parse(dateParts[2]).await();
            return DateTime.create(year, month, day);
        });
    }

    @Override
    public Result<Covid19DailyReport> getDailyReport(DateTime date)
    {
        PreCondition.assertNotNull(date, "date");

        return Result.create(() ->
        {
            final DateTime onlyDate = DateTime.create(date.getYear(), date.getMonth(), date.getDayOfMonth());
            final Iterable<File> dailyReportFiles = this.getDailyReportFiles().await();
            final File dailyReportFile = dailyReportFiles.first((File file) -> onlyDate.equals(Covid19GitDataSource.parseDailyReportFileDate(file).await()));
            if (dailyReportFile == null)
            {
                throw new NotFoundException("No daily report found for the date " + QubCovid19.toString(onlyDate) + ".");
            }
            final Covid19DailyReport result = Covid19DailyReport.parse(dailyReportFile).await();

            PostCondition.assertNotNull(result, "result");

            return result;
        });
    }
}
