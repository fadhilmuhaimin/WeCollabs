package com.app.organizationpanel.ui.organizationPanel

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.app.organizationpanel.R
import com.app.organizationpanel.databinding.ActivityOrganizationPanelBinding
import com.app.organizationpanel.databinding.BottomSheetChooseActionBinding
import com.app.organizationpanel.di.organizationPanelModule
import com.app.organizationpanel.ui.addEvent.AddEventActivity
import com.app.organizationpanel.ui.addProgram.AddProgramActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.core.context.loadKoinModules

class OrganizationPanelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrganizationPanelBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrganizationPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadKoinModules(organizationPanelModule)

        binding.navView.menu.getItem(2).isEnabled = false

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment_activity_organization_panel
        ) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView: BottomNavigationView = binding.navView
        bottomNavigationView.setupWithNavController(navController)

        binding.fabAdd.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun showBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val parent: ViewGroup? = null
        val dialogBinding = BottomSheetChooseActionBinding.inflate(layoutInflater, parent, false)
        with(dialogBinding) {
            addNewEvent.icon.setImageResource(com.app.core.R.drawable.ic_article)
            addNewEvent.title.text = getString(R.string.add_new_event)
            addNewEvent.root.setOnClickListener {
                dialog.dismiss()
                startActivity(Intent(this@OrganizationPanelActivity, AddEventActivity::class.java))
            }
            addNewProgram.icon.setImageResource(com.app.core.R.drawable.ic_event)
            addNewProgram.title.text = getString(R.string.add_new_program)
            addNewProgram.root.setOnClickListener {
                dialog.dismiss()
                startActivity(Intent(this@OrganizationPanelActivity, AddProgramActivity::class.java))
            }
        }
        dialog.apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            setContentView(dialogBinding.root)
            show()
        }
    }
}