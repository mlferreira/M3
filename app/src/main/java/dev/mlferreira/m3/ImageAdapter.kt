package dev.mlferreira.m3

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import dev.mlferreira.m3.entity.Amiibo
import dev.mlferreira.m3.entity.Bank
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class ImageAdapter(
    val context: Context,
    val activeBank: Int,
    val bankCount: Int,
    val allBanks: List<Bank>
) : BaseAdapter() {

    class ViewHolder (
        var image: ImageView,
        var text: TextView
    )

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private val amiiboManager = AmiiboManager(context)

    override fun getCount(): Int {
        return bankCount
    }

    override fun getItem(position: Int): Any {
        return allBanks[position]
    }

    override fun getItemId(position: Int): Long {
        return 0L
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView as? ViewGroup

        val viewHolder: ViewHolder =  if (convertView == null) {
            view = inflater.inflate(R.layout.image_gallery_items, null) as ViewGroup
            val text = (view.findViewById<View>(R.id.grid_text) as TextView)
            val image = (view.findViewById<View>(R.id.grid_bank_image) as ImageView)
            image.setPadding(8, 8, 8, 8)
            ViewHolder(image, text).also { view.tag = it }
        } else {
            convertView.tag as ViewHolder
        }

        try {
            val amiibo: Amiibo = allBanks[position].amiibo

            if (amiibo.id.equals(Amiibo.DUMMY, ignoreCase = true)) {
                viewHolder.text.text = "EMPTY"
                Picasso.get()
                    .load(R.drawable.empty_add)
                    .into(viewHolder.image)
            } else {
                viewHolder.text.text = "Loading..."

                amiiboManager.getAmiibo(amiibo.id).enqueue(
                    object : Callback<Amiibo?> {
                        override fun onResponse(call: Call<Amiibo?>, response: Response<Amiibo?>) {
                            if (!response.isSuccessful) {
                                viewHolder.text.text = "No connection"
                                return
                            }
                            Log.d(this::class.simpleName, "[getView] (amiibo) onResponse success")
                            response.body()
                                ?.let { amiibo ->
                                    viewHolder.text.text = amiibo.name
                                    allBanks[position].amiibo.name = amiibo.name
                                    allBanks[position].amiibo.character = amiibo.character
                                }
                        }

                        override fun onFailure(call: Call<Amiibo?>, th: Throwable) {
                            viewHolder.text.text = "No connection"
                            Picasso.get()
                                .load(R.drawable.empty_unknown)
                                .into(viewHolder.image)
                        }
                    }
                )

                Picasso.get()
                    .load("${Amiibo.API_URL}/images/${amiibo.id}${Amiibo.IMAGE_EXTENSION}")
                    .into(viewHolder.image)

//                amiiboManager.getImage(amiibo.id).enqueue(
//                    object : Callback<ResponseBody?> {
//                        override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
//                            Log.d(this::class.simpleName, "[getView] onResponse")
//                            if (!response.isSuccessful) {
//                                viewHolder.image.setImageResource(R.drawable.empty)
//                                return
//                            }
//                            Log.d(this::class.simpleName, "[getView] (image) onResponse success")
//                            response.body()
////                                ?.bytes()
//                                ?.let { bArr ->
////                                    allBanks[position].amiibo.imageBytes = bArr
//                                    try {
//                                        setScaledImage(viewHolder.image, bArr.bytes())
//                                    } catch (e: Exception) {
//                                        Log.e(this::class.simpleName, "[getView] onResponse - ${e.message}")
//                                    }
//                                }
//                        }
//
//                        override fun onFailure(call: Call<ResponseBody?>, th: Throwable) {
//                            viewHolder.image.setImageResource(R.drawable.empty)
//                        }
//                    }
//                )

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        view!!.setBackgroundResource(if (position == activeBank) 17170450 else 17170445)
        return view
    }


    private fun setScaledImage(imageView: ImageView, bArr: ByteArray) {
        imageView.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                imageView.viewTreeObserver.removeOnPreDrawListener(this)
                val measuredHeight = imageView.measuredHeight
                imageView.setImageBitmap(
                    decodeSampledBitmapFromResource(
                        bArr,
                        imageView.measuredWidth,
                        measuredHeight
                    )
                )
                return true
            }
        })
    }

    private fun setScaledImage(imageView: ImageView, img: Bitmap) {
        imageView.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                imageView.viewTreeObserver.removeOnPreDrawListener(this)
                val measuredHeight = imageView.measuredHeight
                imageView.setImageBitmap(img)
                return true
            }
        })
    }

    private fun decodeSampledBitmapFromResource(bArr: ByteArray, width: Int, height: Int): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(bArr, 0, bArr.size, options)
        options.inSampleSize = calculateInSampleSize(options, width, height)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeByteArray(bArr, 0, bArr.size, options)
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, width: Int, height: Int): Int {
        val outHeight = options.outHeight
        val outWidth = options.outWidth
        var i5 = 1
        if (outHeight > height || outWidth > width) {
            val middleY = outHeight / 2
            val middleX = outWidth / 2
            while (middleY / i5 >= height && middleX / i5 >= width) {
                i5 *= 2
            }
        }
        return i5
    }

}
