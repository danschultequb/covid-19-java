package qub;

/**
 * Application parameters for the qub/covid-19 CLI application.
 */
public class QubCovid19Parameters
{
    private final CharacterWriteStream output;
    private final HttpClient httpClient;
    private final DateTime now;

    /**
     * Create a new application parameters object.
     * @param output The output stream of the application.
     * @param httpClient The HTTP client of the application.
     * @param now The time at which the application is running.
     */
    public QubCovid19Parameters(CharacterWriteStream output, HttpClient httpClient, DateTime now)
    {
        PreCondition.assertNotNull(output, "output");
        PreCondition.assertNotNull(httpClient, "httpClient");
        PreCondition.assertNotNull(now, "now");

        this.output = output;
        this.httpClient = httpClient;
        this.now = now;
    }

    /**
     * Get the output stream of the application.
     * @return The output stream of the application.
     */
    public CharacterWriteStream getOutput()
    {
        return this.output;
    }

    /**
     * Get the HTTP client of the application.
     * @return The HTTP client of the application.
     */
    public HttpClient getHttpClient()
    {
        return this.httpClient;
    }

    /**
     * Get the time at which the application is running.
     * @return The time at which the application is running.
     */
    public DateTime getNow()
    {
        return this.now;
    }
}
