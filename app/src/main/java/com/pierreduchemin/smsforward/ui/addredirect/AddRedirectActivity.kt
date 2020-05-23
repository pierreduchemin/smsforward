package com.pierreduchemin.smsforward.ui.addredirect

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.pierreduchemin.smsforward.R
import kotlinx.android.synthetic.main.redirects_activity.*


class AddRedirectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.redirects_activity)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.findFragmentById(R.id.mainContent) as AddRedirectFragment?
            ?: AddRedirectFragment.newInstance().also {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.mainContent, it)
                }.commit()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_addredirect, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.miContact) {
            val intent = Intent(Intent.ACTION_INSERT_OR_EDIT)
            intent.type = ContactsContract.Contacts.CONTENT_ITEM_TYPE
            startActivity(intent)
        } else {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
