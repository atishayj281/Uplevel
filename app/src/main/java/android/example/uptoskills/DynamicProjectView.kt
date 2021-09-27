package android.example.uptoskills

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginTop

class DynamicProjectView(private val context: Context) {

    fun descriptionEditText(context: Context): EditText {
        val lparams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300)
        val editText: EditText = EditText(context)
        lparams.setMargins(0, 10, 0, 10)
        editText.layoutParams = lparams
        editText.gravity = Gravity.TOP
        val id = 0;
        editText.id = id
        editText.setPadding(10, 5,10,5)
        editText.setBackgroundResource(R.drawable.edittext_round_shape)
        editText.hint = "Please provide the Details and the source link in a proper format"
        return editText
    }

    fun titleEditText(context: Context): EditText {
        val lparams: ViewGroup.LayoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100)
        val editText: EditText = EditText(context)
        editText.layoutParams = lparams
        val id = 0;
        editText.id = id
        editText.setPadding(10, 5,10,5)
        editText.setBackgroundResource(R.drawable.edittext_round_shape)
        editText.hint = "Title"
        return editText
    }

}