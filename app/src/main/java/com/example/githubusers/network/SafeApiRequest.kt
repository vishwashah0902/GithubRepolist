package com.example.githubusers.network

import com.example.githubusers.util.ApiException
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

interface SafeApiRequest {
    suspend fun<T: Any> apiRequest(call: suspend () -> Response<T>) : T{
        val response = call.invoke()
        if (response.isSuccessful){
            return response.body()!!
        }else{
            val error  = response.errorBody().toString()
            val message = StringBuilder()
            error.let {
                try {
                    message.append(JSONObject(it).getString("msg"))
                }catch (e : JSONException){
                }

                message.append("\n")
            }
            message.append("Error Code: ${response.code()}")

            throw ApiException(message.toString())
        }
    }

}