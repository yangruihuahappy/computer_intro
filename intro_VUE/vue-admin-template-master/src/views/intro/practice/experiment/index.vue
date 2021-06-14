<template>
    <div class="dashboard-editor-container">
        <div v-if="admin">
            <tea-experiment/>
        </div>
        <div v-if="!admin">
            <stu-experiment/>
        </div>
        
    </div>
</template>

<script>
import stuExperiment from './stuExperiment'
import teaExperiment from './teaExperiment'
import cookie from 'js-cookie'

export default {
    name: 'Experiment',
    components: {
        stuExperiment,
        teaExperiment,
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