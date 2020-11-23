package qub;

/**
 * A source of data about the Covid-19 pandemic.
 */
public interface Covid19DataSource
{
    /**
     * Get the latest data about the Covid-19 pandemic.
     * @return The result of refreshing the data.
     */
    Result<Void> refreshData(CharacterWriteStream verbose);

    /**
     * Get the basic details of the covid-19 data set.
     * @return The basic details of the covid-19 data set.
     */
    Result<Covid19Summary> getDataSummary(Action1<Covid19Issue> onIssue);

    /**
     * Get the daily report for the provided date.
     * @param date The date to get the daily report for.
     * @return The daily report for the provided date.
     */
    Result<Covid19DailyReport> getDailyReport(DateTime date, Action1<Covid19Issue> onIssue);
}
