<template>
    <div class="dashboard-editor-container">
        <div v-if="admin">
            <tea-timedtest/>
        </div>
        <div v-if="!admin">
            <stu-timedtest/>
        </div>
        
    </div>
</template>

<script>
import stuTimedtest from './stuTimedtest'
import teaTimedtest from './teaTimedtest'
import cookie from 'js-cookie'

export default {
    name: 'Timedtest',
    components: {
        stuTimedtest,
        teaTimedtest,
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