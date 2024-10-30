import { Routes } from '@angular/router';
import { VideolistComponent } from './components/videolist/videolist.component';
import { LoginComponent } from './components/layout/login/login.component';
import { PrincipalComponent } from './components/layout/principal/principal.component';
import { VideodetailComponent } from './components/videodetail/videodetail.component';
import { UservideoComponent } from './components/uservideo/uservideo.component';
import { ChannelsComponent } from './components/channels/channels.component';
import { ProfileComponent } from './components/profile/profile.component';
import { UservideopageComponent } from './components/uservideopage/uservideopage.component';
import { loginGuard } from './auth/login.guard';
import { ManageUserVideosComponent } from './components/manage-user-videos/manage-user-videos.component';

export const routes: Routes = [
  {path: "", redirectTo: "login", pathMatch: 'full'},
  {path: "login", component: LoginComponent},
  {path: 'admin', component: PrincipalComponent, canActivate:[loginGuard], children: [
    {path: "videos", component: VideolistComponent},
    {path: "videos/:id", component: VideodetailComponent},
    {path: "channels", component: ChannelsComponent},
    {path: "uservideos", component: UservideoComponent}, // blocked
    {path: "profile", component: ProfileComponent}, // blocked
    {path: "managevideos", component: ManageUserVideosComponent}, // blocked 
    {path: "videos/by/:username", component: UservideopageComponent}
  ]}
];
