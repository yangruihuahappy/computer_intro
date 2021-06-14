<template>
    <div class="dashboard-editor-container">
        <div v-if="admin">
            <tea-homework/>
        </div>
        <div v-if="!admin">
            <stu-homework/>
        </div>
        
    </div>
</template>

<script>
import stuHomework from './stuHomework'
import teaHomework from './teaHomework'
import cookie from 'js-cookie'

export default {
    name: 'Preview',
    components: {
        stuHomework,
        teaHomework,
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