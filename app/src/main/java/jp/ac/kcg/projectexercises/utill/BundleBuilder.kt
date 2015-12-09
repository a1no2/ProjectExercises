package jp.ac.kcg.projectexercises.utill

import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.util.SparseArray

import java.io.Serializable
import java.util.ArrayList

/**
 * javaDoc書くのめんどいからかかない
 */
class BundleBuilder @JvmOverloads constructor(bundle: Bundle? = null) {
    private val bundle: Bundle
    private var built = false

    init {
        this.bundle = bundle ?: Bundle()
    }

    fun build(): Bundle {
        if (built)
            throw IllegalStateException("おまえはすでにbuild()を呼んでいる...")
        built = true
        return Bundle(bundle)
    }

    fun build(outBundle: Bundle): Bundle {
        if (built)
            throw IllegalStateException("おまえはすでにbuild()を呼んでいる...")
        built = true
        outBundle.putAll(bundle)
        return outBundle
    }


    fun clear(): BundleBuilder {
        bundle.clear()
        return this
    }

    fun remove(key: String): BundleBuilder {
        bundle.remove(key)
        return this
    }

    fun putAll(bundle: Bundle): BundleBuilder {
        this.bundle.putAll(bundle)
        return this
    }

    fun put(key: String, value: IBinder): BundleBuilder {
        bundle.putBinder(key, value)
        return this
    }

    fun put(key: String, value: Bundle): BundleBuilder {
        bundle.putBundle(key, value)
        return this
    }

    fun put(key: String, value: Byte): BundleBuilder {
        bundle.putByte(key, value)
        return this
    }

    fun put(key: String, value: ByteArray): BundleBuilder {
        bundle.putByteArray(key, value)
        return this
    }

    fun put(key: String, value: Char): BundleBuilder {
        bundle.putChar(key, value)
        return this
    }

    fun put(key: String, value: CharArray): BundleBuilder {
        bundle.putCharArray(key, value)
        return this
    }

    fun put(key: String, value: CharSequence): BundleBuilder {
        bundle.putCharSequence(key, value)
        return this
    }

    fun put(key: String, value: Array<CharSequence>): BundleBuilder {
        bundle.putCharSequenceArray(key, value)
        return this
    }


    fun put(key: String, value: Float): BundleBuilder {
        bundle.putFloat(key, value)
        return this
    }

    fun put(key: String, value: FloatArray): BundleBuilder {
        bundle.putFloatArray(key, value)
        return this
    }

    fun put(key: String, value: Parcelable): BundleBuilder {
        bundle.putParcelable(key, value)
        return this
    }

    fun put(key: String, value: Array<Parcelable>): BundleBuilder {
        bundle.putParcelableArray(key, value)
        return this
    }

    fun put(key: String, value: Serializable): BundleBuilder {
        bundle.putSerializable(key, value)
        return this
    }

    fun put(key: String, value: Short): BundleBuilder {
        bundle.putShort(key, value)
        return this
    }

    fun put(key: String, value: ShortArray): BundleBuilder {
        bundle.putShortArray(key, value)
        return this
    }

    fun put(key: String, value: String): BundleBuilder {
        bundle.putString(key, value)
        return this
    }

    fun put(key: String, value: SparseArray<out Parcelable>): BundleBuilder {
        bundle.putSparseParcelableArray(key, value)
        return this
    }


    fun putCharSequeceList(key: String, value: ArrayList<CharSequence>): BundleBuilder {
        bundle.putCharSequenceArrayList(key, value)
        return this
    }

    fun putIntegerList(key: String, value: ArrayList<Int>): BundleBuilder {
        bundle.putIntegerArrayList(key, value)
        return this
    }

    fun putParcelableList(key: String, value: ArrayList<Parcelable>): BundleBuilder {
        bundle.putParcelableArrayList(key, value)
        return this
    }

    fun putStringList(key: String, value: ArrayList<String>): BundleBuilder {
        bundle.putStringArrayList(key, value)
        return this
    }


}
