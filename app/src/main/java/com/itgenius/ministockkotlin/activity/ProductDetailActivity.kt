package com.itgenius.ministockkotlin.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.itgenius.ministockkotlin.databinding.ActivityProductDetailBinding
import com.itgenius.ministockkotlin.network.RetrofitService
import com.itgenius.ministockkotlin.repository.MainRepository
import com.itgenius.ministockkotlin.viewmodel.MainViewModel
import com.itgenius.ministockkotlin.viewmodelfactory.MainViewModelFactory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ProductDetailActivity : AppCompatActivity() {

    // สร้างตัวแปรสำหรับการทำ data binding
    private lateinit var binding:  ActivityProductDetailBinding

    // สร้างตัวแปรไว้เก็บหมายเลข id ที่ส่งมาจาก ProductList
    var id = 0

    // สร้างตัวแปรสำหรับเรียกใช้ viewmodel
    private lateinit var viewModel: MainViewModel

    // สร้างตัวแปรสำหรับเรียกใช้ RetrofitService
    private var retrofilService = RetrofitService.getInstance()

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Data Binding View
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // รับค่า id จาก Product List
        val bundle: Bundle ?= intent.extras
        if(bundle != null) {
            id = bundle.getInt("id")
            println("Product ID: $id")
        }

        // เรียกแสดงผล Toolbar
        setSupportActionBar(binding.toolbar)

        // เรียก ViewModel Provider
        ViewModelProvider(
            this,
            MainViewModelFactory(MainRepository(retrofilService))
        ).get(MainViewModel::class.java).also {
            viewModel = it
        }

        viewModel.productList.observe(this){

            if(supportActionBar != null){
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.setDisplayShowHomeEnabled(true)
                supportActionBar?.title = viewModel.productList.value?.get(0)?.productName.toString()
            }

            // การแปลงตัวเลขจำนวนเงิน
            val priceformatter = NumberFormat.getInstance(Locale.US)
            priceformatter.maximumFractionDigits = 2
            priceformatter.minimumFractionDigits = 2

            // การแปลงวันที่
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val dateformatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

            // ทำการ binding ตัวแปรไปแสดงบน View
            Glide.with(this).load(viewModel.productList.value?.get(0)?.productPicture).into(binding.imgProduct)
            binding.productName.text = viewModel.productList.value?.get(0)?.productName.toString()
            binding.categoryName.text = "หมวดหมู่: " + viewModel.productList.value?.get(0)?.categoryName.toString()
            binding.unitPrice.text = "ราคา: " + priceformatter.format(
                viewModel.productList.value?.get(0)?.unitPrice).toString() + " บาท"
            binding.unitInStock.text = "จำนวน: " + viewModel.productList.value?.get(0)?.unitInStock.toString() + " ชิ้น"
            binding.createdDate.text = "เพิ่มเมื่อ: " + dateformatter.format(
                parser.parse(viewModel.productList.value?.get(0)?.createdDate.toString())!!
            )
        }

        viewModel.errorMessage.observe(this){
            Log.d("Message","Error Call VeiwModel")
        }

        viewModel.getProductById(id)

    }

    // ทำการ Override method onSupportNavigateUp เพื่อจัดการ toolbar เมนู
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}