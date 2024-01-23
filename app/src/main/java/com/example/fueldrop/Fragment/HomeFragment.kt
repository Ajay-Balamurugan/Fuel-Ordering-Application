package com.example.fueldrop.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import com.example.fueldrop.model.menuItem
//import androidx.appcompat.view.menu.MenuAdapter
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fueldrop.adapter.fuel_item_adapter
import com.example.fueldrop.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.fueldrop.adapter.MenuAdapter
import com.google.firebase.database.getValue

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<menuItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        retrieveAndDisplayMenuItems()
        return binding.root
    }

    //retrieve menu items
    private fun retrieveAndDisplayMenuItems(){
        database = FirebaseDatabase.getInstance()
        val fuelRef: DatabaseReference = database.reference.child("menu")
        menuItems = mutableListOf()

        //retrieve menu items from database using loop
        fuelRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(fuelSnapshot in snapshot.children){
                    //val menuItem = fuelSnapshot.getValue(menuItem::class.java)
                    val menuItem = menuItem()
                    menuItem.fuelprice = fuelSnapshot.child("fuelPrice").getValue(String::class.java)
                    menuItem.fuelname = fuelSnapshot.child("fuelName").getValue(String::class.java)
                    menuItem?.let { menuItems.add(menuItem) }
                }
                //display menu items from database using loop
                randomMenuItems()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private fun randomMenuItems() {
        //creating a shuffled list to display elements in random order every time
        val index =menuItems.indices.toList().shuffled()
        val numItemToShow = 5
        val subsetMenuItems = index.take(numItemToShow).map{menuItems[it]}

        setMenuItemsAdapter(subsetMenuItems)
    }
    private lateinit var adapter: fuel_item_adapter
    private fun setMenuItemsAdapter(subsetMenuItems:List<menuItem>) {
        val adapter = MenuAdapter(subsetMenuItems,subsetMenuItems, requireContext())
        binding.FuelRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.FuelRecyclerView.adapter = adapter

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}