local buildImage = 'quay.io/ukhomeofficedigital/hocs-base-image-build';
local testProject() = {
  name: 'test project',
  image: buildImage,
  command: './gradlew clean build --no-daemon'
};

{
  kind: 'pipeline',
  type: 'kubernetes',
  name: 'build',
  trigger: {
    event: ['push'],
    branch: {
      exclude : ['main']
    }
  },
  steps: [
     testProject()
  ]
}
