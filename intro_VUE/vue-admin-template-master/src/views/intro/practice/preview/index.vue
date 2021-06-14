<template>
    <div class="dashboard-editor-container">
        <div v-if="admin">
            <tea-preview/>
        </div>
        <div v-if="!admin">
            <stu-preview/>
        </div>
        
    </div>
</template>

<script>
import stuPreview from './stuPreview'
import teaPreview from './teaPreview'
import cookie from 'js-cookie'

export default {
    name: 'Preview',
    components: {
        stuPreview,
        teaPreview,
    },
    data() {
        return{
            admin: false, //是否是管理员
        }
    },
    created(){
        this.admin = (cookie.get('intro_admin') === "true")
        console.log(this.admin);
    }
}
</script>

<style>

</style>