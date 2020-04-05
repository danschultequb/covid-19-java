package qub;

public class Covid19InMemoryDataSource implements Covid19DataSource
{
    private final MutableMap<DateTime,Covid19DailyReport> dailyReports;

    private Covid19InMemoryDataSource()
    {
        this.dailyReports = Map.create();
    }

    public static Covid19InMemoryDataSource create()
    {
        return new Covid19InMemoryDataSource();
    }

    /**
     * Set the daily report for the provided date.
     * @param date The date to set the daily report for.
     * @param dailyReport The daily report to set.
     * @return This object for method chaining.
     */
    public Covid19InMemoryDataSource setDailyReport(DateTime date, Covid19DailyReport dailyReport)
    {
        PreCondition.assertNotNull(date, "date");
        PreCondition.assertNotNull(dailyReport, "dailyReport");

        date = DateTime.create(date.getYear(), date.getMonth(), date.getDayOfMonth());
        this.dailyReports.set(date, dailyReport);

        return this;
    }

    @Override
    public Result<Void> refreshData()
    {
        return Result.success();
    }

    @Override
    public Result<Covid19Summary> getDataSummary()
    {
        return Result.create(() ->
        {
            final Iterable<DateTime> datesReported = this.dailyReports.getKeys();
            final DateTime mostRecentDateReported = datesReported.maximum(Comparer::compare);

            final Covid19Summary result = Covid19Summary.create()
                .setDatesReportedCount(datesReported.getCount());

            if (mostRecentDateReported != null)
            {
                result.setMostRecentDateReported(mostRecentDateReported);

                final Covid19DailyReport mostRecentDailyReport = this.dailyReports.get(mostRecentDateReported).await();
                result.setCountriesReportedCount(mostRecentDailyReport.getDataRows()
                    .map(Covid19DailyReportDataRow::getCountryOrRegion)
                    .where(Functions.not(Strings::isNullOrEmpty))
                    .toSet()
                    .getCount());
            }

            return result;
        });
    }

    @Override
    public Result<Covid19DailyReport> getDailyReport(DateTime date)
    {
        PreCondition.assertNotNull(date, "date");

        final DateTime onlyDate = DateTime.create(date.getYear(), date.getMonth(), date.getDayOfMonth());
        return this.dailyReports.get(onlyDate)
            .convertError(NotFoundException.class, () -> new NotFoundException("No daily report found for the date " + QubCovid19.toString(onlyDate) + "."));
    }
}
