import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserProfilePhotoComponent } from './user-profile-photo.component';

describe('UserProfilePhotoComponent', () => {
  let component: UserProfilePhotoComponent;
  let fixture: ComponentFixture<UserProfilePhotoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserProfilePhotoComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UserProfilePhotoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
