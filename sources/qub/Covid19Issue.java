package qub;

/**
 * An issue that happens when collecting Covid-19 data.
 */
public class Covid19Issue
{
    private final static String messagePropertyName = "message";
    private final static String filePathPropertyName = "filePath";

    private final String message;
    private Path filePath;

    private Covid19Issue(String message)
    {
        PreCondition.assertNotNullAndNotEmpty(message, "message");

        this.message = message;
    }

    /**
     * Create a new issue.
     * @param message The message that describes the issue.
     * @return The new issue.
     */
    public static Covid19Issue create(String message)
    {
        return new Covid19Issue(message);
    }

    /**
     * Get the message that describes this issue.
     * @return The message that describes this issue.
     */
    public String getMessage()
    {
        return this.message;
    }

    /**
     * Set the file path that this issue comes from.
     * @param filePath The file path that this issue comes from.
     * @return This object for method chaining.
     */
    public Covid19Issue setFilePath(Path filePath)
    {
        PreCondition.assertNotNull(filePath, "file");

        this.filePath = filePath;

        return this;
    }

    /**
     * Get the file that this issue comes from, or null if the issue doesn't come from a file.
     * @return The file that this issue comes from, or null if the issue doesn't come from a file.
     */
    public Path getFilePath()
    {
        return this.filePath;
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof Covid19Issue && this.equals((Covid19Issue)rhs);
    }

    public boolean equals(Covid19Issue rhs)
    {
        return rhs != null &&
            Comparer.equal(this.message, rhs.message) &&
            Comparer.equal(this.filePath, rhs.filePath);
    }

    public JSONObject toJson()
    {
        final JSONObject result = JSONObject.create()
            .setString(Covid19Issue.messagePropertyName, this.message);
        if (this.filePath != null)
        {
            result.setString(Covid19Issue.filePathPropertyName, this.filePath.toString());
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    @Override
    public String toString()
    {
        return this.toJson().toString();
    }
}
