package com.nbs.android.lib.base

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.nbs.android.lib.R
import com.nbs.android.lib.utils.showToast
import java.lang.reflect.ParameterizedType

/**
 * @description:
 * @author:  Mr.x
 * @date :   2020/11/3 9:21 AM
 */
abstract class BaseFragment<VM : BaseViewModel, DB : ViewDataBinding> : Fragment() {
    protected lateinit var mViewModel: VM
    protected lateinit var mDatabinding: DB
    lateinit var mActivity: AppCompatActivity
    private  var dialog: SweetAlertDialog? = null
    private var isShown = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mDatabinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        mDatabinding.lifecycleOwner = this
        return mDatabinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        mDatabinding.setVariable(initVariableId(),mViewModel)
        registorUIChangeLiveDataCallBack()
        initView()
        initData()
        initObserve()
    }

    abstract fun getLayoutId(): Int

    abstract fun initView()

    abstract fun initData()

    abstract fun initVariableId(): Int
    abstract fun initObserve()

    private fun initViewModel() {
        val types = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
        mViewModel = ViewModelProvider(this).get<VM>(types[0] as Class<VM>)
    }


    open fun onShow() {}
    fun onHide() {}
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            isShown = true
            onShow()
        } else {
            isShown = false
            onHide()
        }
    }
    //注册ViewModel与View的契约UI回调事件
    protected open fun registorUIChangeLiveDataCallBack() { //加载对话框显示
        mViewModel.uc.showDialogEvent.observe(this,
            Observer<Int> { title -> showDialog(title) })

        mViewModel.uc.showDialogStrEvent.observe(this,
            Observer<String> { t -> showDialog(t) })
        mViewModel.uc.showDialogNotCancelEvent.observe(this,
            Observer<Int> { titleId -> showDialog(titleId) })
        //加载对话框消失
        mViewModel.uc.dismissDialogEvent
            .observe(this, Observer<Long> { dismissDialog() })


        mViewModel.uc.toastEvent.observe(this,
            Observer<Int> { msgId -> context?.showToast(msgId) })
        //跳入新页面
        mViewModel.uc.startActivityEvent.observe(
            this,
            Observer<Map<String, Any>> { params ->
                val clz =
                    params[ParameterField.CLASS] as Class<*>
                val bundle = params[ParameterField.BUNDLE] as Bundle?
                startActivity(clz, bundle)
            })

        mViewModel.uc.startWebActivityEvent.observe(
            this, Observer<String> { url ->
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                intent.data = Uri.parse(url)
                startActivity(intent, null)
            })

        //关闭界面
        mViewModel.uc.finishEvent.observe(this,
            Observer<Void> { mActivity.finish() })
        //关闭上一层
        mViewModel.uc.onBackPressedEvent
            .observe(this,
                Observer<Void> { mActivity.onBackPressed() })
    }



    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    open fun startActivity(clz: Class<*>?) {
        startActivity(Intent(mActivity, clz))
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    open fun startActivity(clz: Class<*>?, bundle: Bundle?) {
        val intent = Intent(mActivity, clz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        println("~~~~~~~~~~~~startActivity")
        startActivity(intent)
    }
    open fun showDialog(titleId: Int = R.string.LOADING) {
        showDialog(getString(titleId))
    }
    fun showDialog(title:String){
        if (dialog == null) {
            dialog = SweetAlertDialog(mActivity, SweetAlertDialog.PROGRESS_TYPE)
            dialog?.let {
                it.progressHelper.barColor = resources.getColor(R.color.colorAccent, null)
                it.titleText = title
                it.setCancelable(false)
                it.show()
            }

        } else {
            dialog?.let {
                it.titleText = title
                it.show()
            }
        }
    }

    open fun dismissDialog() {
        if (dialog != null && dialog!!.isShowing) {
            dialog?.dismiss()
        }
    }

}