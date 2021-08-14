import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StampGet(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("info")
    val info: String,
    @SerializedName("status")
    val status: Int
) : Serializable {
    data class Data(
        @SerializedName("date")
        val date: Int,
        @SerializedName("task_income")
        val taskIncome: Int,
        @SerializedName("task_name")
        val taskName: String
    ) : Serializable
}