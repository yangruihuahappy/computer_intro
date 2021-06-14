<template>
    <div class="dashboard-editor-container">
        <div v-if="admin">
            <tea-summary/>
        </div>
        <div v-if="!admin">
            <stu-summary/>
        </div>
        
    </div>
</template>

<script>
import stuSummary from './stuSummary'
import teaSummary from './teaSummary'
import cookie from 'js-cookie'

export default {
    name: 'Summary',
    components: {
        stuSummary,
        teaSummary,
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