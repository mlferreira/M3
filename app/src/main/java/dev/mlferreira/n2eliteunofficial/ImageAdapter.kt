package dev.mlferreira.n2eliteunofficial

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import dev.mlferreira.n2eliteunofficial.entity.Amiibo
import dev.mlferreira.n2eliteunofficial.entity.Bank
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

            if (amiibo.hexId.equals(Amiibo.DUMMY, ignoreCase = true)) {
                viewHolder.text.text = "EMPTY"
            } else {
                viewHolder.text.text = "Loading..."

                val queue = Volley.newRequestQueue(context)

                val stringRequest = StringRequest(
                    Request.Method.GET,
                    "${Amiibo.API_URL}?id=${amiibo.hexId}",
                    { response ->

//                        val resp = JsonParser.parseString(response)
//
//                        val json: JsonObject = if (resp.isJsonArray) {
//                            resp.asJsonArray.first().asJsonObject
//                        } else {
//                            resp.asJsonObject
//                        }.get("amiibo").asJsonArray.first().asJsonObject
//

//                        amiibo.name = json.get("name").asString
                        amiibo.name = response.substringAfter("\"name\":").substringAfter('"').substringBefore('"')
                        viewHolder.text.text = amiibo.name

//                        amiibo.imageUrl = json.get("image").asString
//                        amiibo.imageUrl = response.substringAfter("\"image\":").substringAfter('"').substringBefore('"')
//                        setImage(viewHolder.image, amiibo.imageUrl)

                    },
                    { e -> Toast.makeText(context, "failed ${amiibo.hexId} - ${e.networkResponse.statusCode}", Toast.LENGTH_LONG).show() }
                )

                queue.add(stringRequest)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        view!!.setBackgroundResource(if (position == activeBank) 17170450 else 17170445)
        return view
    }

    private fun setImage(imageView: ImageView, imgUrl: String?) {
        if (imgUrl == null) {
            imageView.setImageResource(R.drawable.empty)
            return
        }

        val queue = Volley.newRequestQueue(context)

        val stringRequest = ImageRequest(
            imgUrl,
            Response.Listener<Bitmap> { image ->
                setScaledImage(imageView, image)
            },
            0,
            0,
            ImageView.ScaleType.CENTER_INSIDE,
            Bitmap.Config.RGB_565,
            Response.ErrorListener { imageView.setImageResource(R.drawable.empty) }
        )

        queue.add(stringRequest)

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
