package qub;

/**
 * The summary data from a Covid19DataSource.
 */
public class Covid19Summary
{
    private int datesReportedCount;
    private int countriesReportedCount;
    private DateTime mostRecentDateReported;

    private Covid19Summary()
    {
    }

    /**
     * Create a new Covid19DataSummary object.
     * @return A new Covid19DataSummary object.
     */
    public static Covid19Summary create()
    {
        return new Covid19Summary();
    }

    /**
     * Get the number of dates that have been reported.
     * @return the number of dates that have been reported.
     */
    public int getDatesReportedCount()
    {
        return this.datesReportedCount;
    }

    /**
     * Set the number of dates that have been reported.
     * @param datesReportedCount The number of dates that have been reported.
     * @return This object for method chaining.
     */
    public Covid19Summary setDatesReportedCount(int datesReportedCount)
    {
        PreCondition.assertGreaterThanOrEqualTo(datesReportedCount, 0, "datesReportedCount");

        this.datesReportedCount = datesReportedCount;

        return this;
    }

    /**
     * Get the number of countries that have been reported.
     * @return The number of countries that have been reported.
     */
    public int getCountriesReportedCount()
    {
        return this.countriesReportedCount;
    }

    /**
     * Set the number of countries that have been reported.
     * @param countriesReportedCount The number of countries that have been reported.
     * @return This object for method chaining.
     */
    public Covid19Summary setCountriesReportedCount(int countriesReportedCount)
    {
        PreCondition.assertGreaterThanOrEqualTo(countriesReportedCount, 0, "countriesReportedCount");

        this.countriesReportedCount = countriesReportedCount;

        return this;
    }

    /**
     * Get the most recent date reported.
     * @return The most recent date reported.
     */
    public DateTime getMostRecentDateReported()
    {
        final DateTime result = this.mostRecentDateReported;

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    /**
     * Set the most recent date reported.
     * @param mostRecentDateReported The most recent date reported.
     * @return This object for method chaining.
     */
    public Covid19Summary setMostRecentDateReported(DateTime mostRecentDateReported)
    {
        PreCondition.assertNotNull(mostRecentDateReported, "mostRecentDateReported");

        this.mostRecentDateReported = mostRecentDateReported;

        return this;
    }
}
