package pica.pica.util
import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import pica.pica.R

class IndeterminateProgressDialog(context: Context) : AlertDialog(context) {

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.loading, null)
        setView(view)
    }

}