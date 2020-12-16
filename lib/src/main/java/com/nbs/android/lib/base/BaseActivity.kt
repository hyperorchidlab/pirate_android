package com.nbs.android.lib.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kongzue.dialog.v3.TipDialog
import com.kongzue.dialog.v3.WaitDialog
import com.nbs.android.lib.R
import com.nbs.android.lib.utils.AppManager
import com.nbs.android.lib.utils.showToast
import java.lang.reflect.ParameterizedType

/**
 * @description:
 * @author:  Mr.x
 * @date :   2020/11/3 8:01 AM
 */
abstract class BaseActivity<VM : BaseViewModel, DB : ViewDataBinding> : AppCompatActivity() {
    val TAG = this.javaClass.name
    protected val mViewModel: VM by lazy {
        val types = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
        ViewModelProvider(this).get<VM>(types[0] as Class<VM>)
    }

    protected lateinit var mDataBinding: DB
    private var viewModelId = 0
    protected  var dialog: TipDialog? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppManager.addActivity(this)
        onContentViewBefor(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        mDataBinding.lifecycleOwner = this

        viewModelId = initVariableId()
        mDataBinding.setVariable(viewModelId, mViewModel)
        registorUIChangeLiveDataCallBack()
        initView()
        initData()
        initObserve()
    }

    abstract fun getLayoutId(): Int

    abstract fun initView()

    abstract fun initData()
    abstract fun initObserve()

    abstract fun initVariableId(): Int
    protected open fun onContentViewBefor(savedInstanceState: Bundle?) {}

    open fun <T : ViewModel> createViewModel(cls: Class<T>): T {
        return ViewModelProvider(this).get(cls)
    }

    //注册ViewModel与View的契约UI回调事件
    protected open fun registorUIChangeLiveDataCallBack() { //加载对话框显示
        mViewModel.uc.showDialogEvent.observe(this, object : Observer<Int> {
            override fun onChanged(@Nullable titleId: Int) {
                showDialog(resources.getString(titleId),true)
            }
        })

        mViewModel.uc.showDialogNotCancelEvent.observe(this, object : Observer<Int> {
            override fun onChanged(@Nullable titleId: Int) {
                showDialog(resources.getString(titleId),false)
            }
        })

        mViewModel.uc.showDialogNotCancelStrEvent.observe(this, object : Observer<String> {
            override fun onChanged(@Nullable title: String) {
                showDialog(title,false)
            }
        })

        //加载对话框消失
        mViewModel.uc.dismissDialogEvent
            .observe(this, object : Observer<Long> {
                override fun onChanged(@Nullable v: Long) {
                    dismissDialog()
                }
            })

        mViewModel.uc.toastEvent.observe(this, object : Observer<Int> {
            override fun onChanged(msgId: Int) {
                showToast(msgId)
            }

        })
        //跳入新页面
        mViewModel.uc.startActivityEvent.observe(
            this, Observer<Map<String, Any>> { params ->
                val clz = params[ParameterField.CLASS] as Class<*>
                val bundle = params[ParameterField.BUNDLE] as Bundle?
                startActivity(clz, bundle)
            })

        mViewModel.uc.startWebActivityEvent.observe(this, object : Observer<String> {
                override fun onChanged(@Nullable url: String) {
                    val intent = Intent()
                    intent.action = "android.intent.action.VIEW"
                    intent.data = Uri.parse(url)
                    startActivity(intent, null)
                }
            })
        //关闭界面
        mViewModel.uc.finishEvent.observe(this, object : Observer<Void> {
            override fun onChanged(@Nullable v: Void?) {
                finish()
            }
        })
        //关闭上一层
        mViewModel.uc.onBackPressedEvent
            .observe(this, object : Observer<Void> {
                override fun onChanged(@Nullable v: Void) {
                    onBackPressed()
                }
            })
    }

    @JvmOverloads
    open fun showDialog(title: String = getString(R.string.loading),cancelable: Boolean = true) {
        dialog= WaitDialog.show(this,title)
        dialog?.cancelable = cancelable
    }

    open fun dismissDialog() {
        if (dialog != null && dialog!!.isShow) {
            dialog?.doDismiss()
        }
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    open fun startActivity(clz: Class<*>?) {
        startActivity(Intent(this, clz))
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    open fun startActivity(clz: Class<*>?, bundle: Bundle?) {
        val intent = Intent(this, clz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }


    override fun onDestroy() {
        super.onDestroy()
        AppManager.removeActivity(this)
    }

}