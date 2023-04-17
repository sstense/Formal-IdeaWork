<template>
    <div>
        <div style="margin-bottom: 5px">
            <el-input v-model="name" placeholder="请输入物品名" suffix-icon="el-icon-search" style="width:200px"
                      @keyup.enter.native="loadPost"></el-input>
            <el-select v-model="storage" placeholder="请选择仓库" style="margin-left: 5px">
                <el-option
                        v-for="item in storageData"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id">
                </el-option>
            </el-select>
            <el-select v-model="goodstype" placeholder="请选择分类" style="margin-left: 5px">
                <el-option
                        v-for="item in goodstypeData"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id">
                </el-option>
            </el-select>
            <el-button type="primary" style="margin-left: 5px" @click="loadPost">查询</el-button>
            <el-button type="success" @click="resetParam">重置</el-button>

            <el-button type="primary" style="margin-left: 5px" @click="add" v-if="user.roleId!=2">新增</el-button>
            <el-button type="primary" style="margin-left: 5px" @click="inGoods" v-if="user.roleId!=2">入库</el-button>
            <el-button type="primary" style="margin-left: 5px" @click="outGoods" v-if="user.roleId!=2">出库</el-button>
        </div>
        <el-table :data="tableData"
                  :header-cell-style="{backgroundColor : '#f2f5fc'}"
                  border
                  highlight-current-row
                  @current-change="selectCurrentChange">
            <el-table-column prop="id" label="ID" width="60">
            </el-table-column>
            <el-table-column prop="name" label="物品名" width="180">
            </el-table-column>
            <el-table-column prop="storage" label="仓库" width="180" :formatter="formatterStorage">
        </el-table-column>
            <el-table-column prop="goodstype" label="分类" width="180" :formatter="formatterType">
            </el-table-column>
            <el-table-column prop="count" label="数量" width="180">
            </el-table-column>
            <el-table-column prop="remark" label="备注">
            </el-table-column>
            <el-table-column prop="operate" label="操作" v-if="user.roleId!=2">
                <template slot-scope="scope">
                    <el-button size="small" type="success" @click="mod(scope.row)">编辑</el-button>
                    <el-popconfirm
                            title="确定删除吗？"
                            @confirm="del(scope.row.id)"
                            style="margin-left: 5px"
                    >
                        <el-button slot="reference" size="small" type="danger" >删除</el-button>
                    </el-popconfirm>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="pageNum"
                :page-sizes="[5, 10, 20,30]"
                :page-size="pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total">
        </el-pagination>

        <el-dialog
                title="商品维护"
                :visible.sync="centerDialogVisible"
                width="30%"
                center
                @close="rest">
            <el-form ref="form" :rules="rules" :model="form" label-width="80px" >
                <el-form-item prop="id"></el-form-item>
                <el-form-item label="物品名" prop="name">
                    <el-col :span="20">
                        <el-input v-model="form.name" :disabled="isInput"></el-input>
                    </el-col>
                </el-form-item>
                <el-form-item label="仓库" prop="storage">
                    <el-col :span="20">
                        <el-select v-model="form.storage" placeholder="请选择仓库" style="margin-left: 5px">
                            <el-option
                                    v-for="item in storageData"
                                    :key="item.id"
                                    :label="item.name"
                                    :value="item.id">
                            </el-option>
                        </el-select>
                    </el-col>
                </el-form-item>
                <el-form-item label="分类" prop="goodstype">
                    <el-col :span="20">
                        <el-select v-model="form.goodstype" placeholder="请选择分类" style="margin-left: 5px">
                            <el-option
                                    v-for="item in goodstypeData"
                                    :key="item.id"
                                    :label="item.name"
                                    :value="item.id">
                            </el-option>
                        </el-select>
                    </el-col>
                </el-form-item>
                <el-form-item label="数量" prop="count">
                    <el-col :span="20">
                        <el-input v-model="form.count" ></el-input>
                    </el-col>
                </el-form-item>
                <el-form-item label="备注" prop="remark">
                    <el-col :span="20">
                        <el-input type="textarea" v-model="form.remark"></el-input>
                    </el-col>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
    <el-button @click="rest">取 消</el-button>
    <el-button type="primary" @click="save">确 定</el-button>
  </span>
        </el-dialog>

        <el-dialog
                title="出入库"
                :visible.sync="inDialogVisible"
                width="30%"
                center
                @close="rest1">

            <el-dialog
                    width="50%"
                    title="用户选择"
                    :visible.sync="innerVisible"
                    append-to-body
            >

                <SelectUserManage @doSelectUser="doSelectUser"></SelectUserManage>
                <span slot="footer" class="dialog-footer">
    <el-button @click="rest2">取 消</el-button>
    <el-button type="primary" @click="confirmUser">确 定</el-button>
  </span>
            </el-dialog>

            <el-form ref="form1" :rules="rules1" :model="form1" label-width="80px" >
                <!--<el-form-item prop="id"></el-form-item>-->
                <el-form-item label="物品名" >
                    <el-col :span="20">
                        <el-input v-model="form1.goodsname" :disabled="isInput"></el-input>
                    </el-col>
                </el-form-item>
                <el-form-item label="申请人" prop="userName" >
                    <el-col :span="20">
                        <el-input v-model="form1.userName"  @click.native="selectUser"></el-input>
                    </el-col>
                </el-form-item>
                <el-form-item label="数量" prop="count">
                    <el-col :span="20">
                        <el-input v-model="form1.count" ></el-input>
                    </el-col>
                </el-form-item>
                <el-form-item label="备注" prop="remark">
                    <el-col :span="20">
                        <el-input type="textarea" v-model="form1.remark"></el-input>
                    </el-col>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
    <el-button @click="rest1">取 消</el-button>
    <el-button type="primary" @click="doInGoods">确 定</el-button>
  </span>
        </el-dialog>
    </div>
</template>

<script>
    import SelectUserManage from "../user/SelectUserManage";
    export default {
        name: "GoodsManage",
        components: {SelectUserManage},
        data() {
            /*let checkAge = (rule, value, callback) => {
                if(value>150){
                    callback(new Error('年龄输⼊过⼤'));
                }else{
                    callback();
                }
            };*/
            let checkDuplicate =(rule,value,callback)=>{
                if(this.form.id){
                    return callback();
                }
                this.$axios.get(this.$httpUrl+"/goods/findByName?name="+this.form.name).then(res=>res.data).then(res=>{
                    if(res.code!=200){
                        callback()
                    }else{
                        callback(new Error('物品已经存在'));
                    }
                })
            };
            let checkCount = (rule, value, callback) => {
                if(value>9999){
                    callback(new Error('数量输⼊过⼤'));
                }else{
                    callback();
                }
            };
            return {
                user: JSON.parse(sessionStorage.getItem("CurUser")),
                goodstypeData:[],
                storageData:[],
                goodstype:'',
                storage:'',
                tableData: [],
                pageSize: 10,
                pageNum:1,
                total:0,
                name:'',
                centerDialogVisible:false,
                inDialogVisible:false,
                innerVisible:false,
                currentRow:{},
                temperUser:{},
                form:{
                    id:'',
                    name:'',
                    storage:'',
                    goodstype:'',
                    count:'',
                    remark:''
                },
                form1:{
                    goods:'',
                    goodsname:'',
                    count:'',
                    userName:'',
                    userid:'',
                    adminId:'',
                    remark:'',
                    action:'1'
                },
                rules1:{

                },
                 rules: {
                     name: [
                         {required: true, message: '请输入物品名', trigger: 'blur'},
                         {validator:checkDuplicate,trigger: 'blur'}
                     ],
                     goodstype:[
                         {required: true, message: '请选择分类', trigger: 'blur'},
                     ],
                     storage:[
                         {required: true, message: '请选择仓库', trigger: 'blur'},
                     ],
                     count: [
                         {required: true, message: '请输⼊数量', trigger: 'blur'},
                         {pattern: /^([1-9][0-9]*){1,4}$/,message: '数量必须为正整数字',trigger: "blur"},
                         {validator:checkCount,trigger: 'blur'}
                     ],
                 },
                isInput:false
            }
        },
        methods:{
            doSelectUser(val){
                //console.log(val)
                this.temperUser=val
            },
            confirmUser(){
                this.form1.userName=this.temperUser.name
                this.form1.userid=this.temperUser.id

                this.innerVisible=false
            },
            selectCurrentChange(val) {
                this.currentRow = val;
            },
            formatterStorage(row){
                let temp=this.storageData.find(item=>{
                    return item.id == row.storage
                })

                return temp && temp.name
            },
            formatterType(row){
                let temp=this.goodstypeData.find(item=>{
                    return item.id == row.goodstype
                })

                return temp && temp.name
            },
            resetForm() {
                this.$refs.form.resetFields();
            },
            rest(){
                this.centerDialogVisible = false;
                this.resetForm();
                //this.form.id=''
            },
            resetInForm() {
                this.$refs.form1.resetFields()
            },
            rest1(){
                this.inDialogVisible = false;
                this.resetInForm();
                //this.form.id=''
            },
            /*resetInnerForm() {
                this.$refs.form.resetFields();
            },*/
            rest2(){
                this.innerVisible = false;
                //this.resetInnerForm();
                //this.form.id=''
            },
            del(id){
                this.$axios.get(this.$httpUrl+'/goods/del?id='+id).then(res=>res.data).then(res=>
                {
                    console.log(res)
                    if(res.code==200){
                        this.$message({
                            message: '操作成功！',
                            type: 'success'
                        });

                        this.loadPost()
                    }else{
                        this.$message.error('操作失败');
                    }
                })
            },
            mod(row){
                //赋值到表单

                this.centerDialogVisible=true
                this.isInput=true

                this.$nextTick(()=>{
                    this.form.id=row.id
                    this.form.name=row.name
                    this.form.storage=row.storage
                    this.form.goodstype=row.goodstype
                    this.form.count=row.count
                    this.form.remark=row.remark
                })
            },
            add(){
                this.centerDialogVisible=true
                this.isInput=false

                this.$nextTick(()=>{
                    this.resetForm()
                })
            },
            inGoods(){
                if(!this.currentRow.id){
                    alert('请选择一条记录')
                    return
                }
                this.inDialogVisible=true
                this.isInput=true

                this.$nextTick(()=>{
                    this.resetInForm()
                })

                this.form1.goodsname=this.currentRow.name
                this.form1.goods=this.currentRow.id
                this.form1.adminId=this.user.id
                this.form1.action='1'
            },
            outGoods(){
                if(!this.currentRow.id){
                    alert('请选择一条记录')
                    return
                }
                this.inDialogVisible=true
                this.isInput=true

                this.$nextTick(()=>{
                    this.resetInForm()
                })

                this.form1.goodsname=this.currentRow.name
                this.form1.goods=this.currentRow.id
                this.form1.adminId=this.user.id
                this.form1.action='2'
            },
            selectUser(){
                this.innerVisible=true
            },
            doSave(){
                this.$axios.post(this.$httpUrl+'/goods/save',this.form).then(res=>res.data).then(res=>
                {
                    console.log(res)
                    if(res.code==200){
                        this.$message({
                            message: '操作成功！',
                            type: 'success'
                        });

                        this.centerDialogVisible = false
                        this.loadPost()
                    }else{
                        this.$message.error('操作失败');
                    }
                })
            },
            doMod(){
                this.$axios.post(this.$httpUrl+'/goods/update',this.form).then(res=>res.data).then(res=>
                {
                    console.log(res)
                    if(res.code==200){
                        this.$message({
                            message: '操作成功！',
                            type: 'success'
                        });

                        this.centerDialogVisible = false
                        this.loadPost()
                    }else{
                        this.$message.error('操作失败');
                    }
                    //this.form.id=''
                    this.rest()
                })
            },
            save(){
                this.$refs.form.validate((valid) => {
                    if (valid) {
                        if(this.form.id){
                            this.doMod();
                        }
                        else{
                            this.doSave();
                        }
                    } else {
                        console.log('error submit!!');
                        return false;
                    }
                });

            },
            doInGoods(){
                this.$axios.post(this.$httpUrl+'/record/save',this.form1).then(res=>res.data).then(res=>
                {
                    console.log(res)
                    if(res.code==200){
                        this.$message({
                            message: '操作成功！',
                            type: 'success'
                        });

                        this.inDialogVisible = false
                        this.loadPost()
                        this.resetInForm()
                    }else{
                        this.$message.error('操作失败');
                    }
                })
            },
            handleSizeChange(val) {
                console.log(`每页 ${val} 条`);
                this.pageNum=1
                this.pageSize=val
                this.loadPost()
            },
            handleCurrentChange(val) {
                console.log(`当前页: ${val}`);
                this.pageNum=val
                this.loadPost()
            },
            resetParam(){
                this.name=''
                this.goodstype=''
                this.storage=''
            },
            loadPost(){
                this.$axios.post(this.$httpUrl+'/goods/listPage',{
                    pageSize:this.pageSize,
                    pageNum:this.pageNum,
                    param:{
                        name:this.name,
                        goodstype:this.goodstype + '',
                        storage:this.storage + '',
                    }
                }).then(res=>res.data).then(res=>{
                    console.log(res)
                    if(res.code==200){
                        this.tableData=res.data
                        this.total=res.total
                    }else{
                        alert('获取数据失败')
                    }
                })
            },
            loadStorage(){
                this.$axios.get(this.$httpUrl+'/storage/list').then(res=>res.data).then(res=>{
                    console.log(res)
                    if(res.code==200){
                        this.storageData=res.data
                        this.total=res.total
                    }else{
                        alert('获取数据失败')
                    }
                })
            },
            loadGoodstype(){
                this.$axios.get(this.$httpUrl+'/goodstype/list').then(res=>res.data).then(res=>{
                    console.log(res)
                    if(res.code==200){
                        this.goodstypeData=res.data
                        this.total=res.total
                    }else{
                        alert('获取数据失败')
                    }
                })
            }
        },
        beforeMount() {
            //this.loadGet();
            this.loadGoodstype();
            this.loadStorage();
            this.loadPost();
        }
    }
</script>

<style scoped>

</style>