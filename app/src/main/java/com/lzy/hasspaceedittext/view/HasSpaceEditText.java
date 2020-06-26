package com.lzy.hasspaceedittext.view;

import android.content.Context;
import android.graphics.Paint;
import android.icu.util.Measure;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.TtsSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * 带空格的EditText
 */
public class HasSpaceEditText extends AppCompatEditText {
    private static final String TAG = "HasSpaceEditText";
    private int[] regulation; //分隔的规则 比如手机号码 137 3333 3333,它的规律就是{3,4,4}
    private List<Integer> deleteIndexes; //需要在此处删除的索引集合
    private int[] addIndexes; // 需要在此处添加的索引集合
    private String separator; //分隔符号,默认为为一个空格 " "
    private int beforeCSLength; //改变之前string的长度

    public HasSpaceEditText(Context context) {
        this(context, null);
    }

    public HasSpaceEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HasSpaceEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    /**
     * 初始化数组 regulation addIndexes addIndexes
     */
    private void initArray() {
        if (regulation == null || regulation.length == 0){
            throw new RuntimeException("regulation不能为空");
        }
        if (TextUtils.isEmpty(separator)){
            separator = " ";
        }

        deleteIndexes = new ArrayList<>();
        addIndexes = new int[regulation.length];

        //把规则(规律数据)转换成长度数据
        for (int i = 0; i < regulation.length; i++) {
            if (i == 0) {
                addIndexes[i] = regulation[i];
            } else {
                addIndexes[i] = addIndexes[i - 1] + regulation[i];
            }
        }

        //计算需要插入间隔的坐标数据
        for (int i = 0; i < addIndexes.length; i++) {
            addIndexes[i] = addIndexes[i] + i * separator.length();
            Log.d(TAG, "init: addIndexes[i] = " + addIndexes[i]);
        }
    }

    private void initTextListener() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: s = " + s);
                beforeCSLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: s = " + s);
                Log.d(TAG, "onTextChanged: start =" + start );
                //通过比对beforeTextChanged和onTextChanged中的字符串来判断是增加还是删除
                if (beforeCSLength < s.length()) { //增加字符
                    if (start != s.length() - 1 && s.length() > 0){
                        addCharFromMiddle();
                    }else {
                        addChar(s.length());
                    }

                } else if (beforeCSLength > s.length()) { //删除字符
                    if (start != s.length()){
                        deleteCharFromMiddle();
                    }else {
                        deleteChar(s.length());
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 删除字符串的时候
     *
     * @param length cursor的索引
     */
    private void deleteChar(int length) {
        Log.d(TAG, "deleteChar:");
        for (int i = 0; i < deleteIndexes.size(); i++) {
            //提前一个位置删除
            if (length == deleteIndexes.get(i)) {
                Log.d(TAG, "/deleteIndexes.get(i) =  " + deleteIndexes.get(i));
                //假如现在为 133 3333,当删除时,cursor回退到4的位置时,删除3-4的空格
                getEditableText().delete(deleteIndexes.get(i) - separator.length(), deleteIndexes.get(i));
                deleteIndexes.set(i, -1);
                break;
            }
        }
    }

    //从中间删除
    private void deleteCharFromMiddle(){
        Toast.makeText(getContext(),"从中间删除的功能尚未实现",Toast.LENGTH_SHORT).show();
    }

    /**
     * 增加字符串的时候
     *
     * @param length cursor的索引
     */
    private void addChar(int length) {
        Log.d(TAG, "addChar: ");

        /*
        * 每次调用append,insert,delete等方法,会重新回调TextWatcher中的各个回调方法,这个要注意
        */

        for (int i = 0; i < addIndexes.length; i++) {
            if (i != addIndexes.length - 1) { //最后一个不需要插入分隔
                //提前一个位置插入,输入第二个三触发
                if (length == addIndexes[i]) {
                    append(separator);
                    //添加时,把将来需要删除的位置,记录下来,删除时的位置比addIndexes的对应位置多separator.length()
                    deleteIndexes.add(i, addIndexes[i] + separator.length());
                    break;
                }

                //删除到节点却没有空格的情况,需要重新插入空格
                if (length <= separator.length() || TextUtils.isEmpty(getText())){
                    continue;
                }

                //截取当前字符串最后的 separator.length()个单位,例如 "133 ",就是截取最后的空格
                CharSequence sub = getText().subSequence(length - separator.length(),length).toString();
                //判断是否已经含有分隔符,若没有,则重新插入
                if (length == addIndexes[i] + separator.length() && !separator.equals(sub)){
                    getEditableText().insert(length-1,separator);
                }

            }
        }
    }

    private void addCharFromMiddle(){
        Toast.makeText(getContext(),"从中间添加的功能尚未实现",Toast.LENGTH_SHORT).show();
        /*
        * 思路:
        * 1,用stringbuilder 记录字符串
        * 2,调整空格,判断原有空格位置是否合理,若不合理删除
        * 3,重新再合适的位置插入空格
        * 4,settext 设置给控件
        * */
    }


    /**
     * 设置规律数组 例如 133 3333 3333 数组为{3,4,4}
     * @param regulation 规律数组,数组中的最后一个元素是忽略掉的,因为最后不需要插入分隔符
     */
    public HasSpaceEditText setRegulation(int[] regulation) {
        this.regulation = regulation;
        return this;
    }

    /**
     * 设置分隔符 例如" ",单个空格
     * 注意分隔符的长度不能大于等于regulation中最短元素的长度,否则会出现问题
     */
    public HasSpaceEditText setSeparator(String separator){
        this.separator = separator;
        return this;
    }

    public void build(){
        initArray();
        initTextListener();
    }

    /**
     * 获取剔除掉分隔符后的字符串
     * @return text
     */
    public String getNoSpaceText() {
        CharSequence charSequence = getText();
        if (!TextUtils.isEmpty(charSequence)) {
            String s = charSequence.toString();
            if (s.contains(separator)) {
                return s.replace(separator, "");
            } else {
                return s;
            }

        }
        return "";
    }

}
